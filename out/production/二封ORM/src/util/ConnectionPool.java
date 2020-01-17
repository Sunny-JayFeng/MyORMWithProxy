package util;

import readproperties.ReadDBConfig;
import myexception.FreeConnectionException;
import java.sql.Connection;

public class ConnectionPool {
    private static final byte BUSY_VALUE = 1;
    private static final byte FREE_VALUE = 0;
    private static final byte NULL_VALUE = -1;
    private static Connection[] connectionList = new MyConnection[ReadDBConfig.getInstance().getConnectionCount("minConnectionSize","1")];
    private static byte[] connectionBitMap = new byte[ReadDBConfig.getInstance().getConnectionCount("minConnectionSize","1")];
    private static int total = 0; // 已有连接数

    // 初始化状态数组里所有的状态为-1
    static {
        for(int i = 0; i < connectionBitMap.length; i ++){
            connectionBitMap[i] = -1;
        }
    }

    // 该方法用于遍历轮询状态数组，返回符合要求(空闲/空置)的连接的下标
    private static int getStatusIndex(byte status){
        for(int index = 0; index < connectionBitMap.length; index ++){
            if(connectionBitMap[index] == status){
                return index; // 找到了，返回对应下标
            }
        }
        // 如果找不到，返回无穷
        return Integer.MAX_VALUE;
    }
    // 获取空闲连接下标
    private static int getFreeIndex(){
        return getStatusIndex(FREE_VALUE);
    }
    // 获取空置连接的下标
    private static int getNullIndex(){
        return getStatusIndex(NULL_VALUE);
    }

    // 该方法用于分配连接
    private static synchronized Connection getDistribute(int index){
        // 严谨性判断，再次判断得到的连接下标是否还是非忙碌的
        if(connectionBitMap[index] == BUSY_VALUE){  // 如果它变成忙碌的了，那直接返回 null
            return null;
        }else if(connectionBitMap[index] == NULL_VALUE){ // 如果它还是空置的，创建一个新连接
            Connection newConnection = new MyConnection(index);
            total ++; // 创建新连接，已有连接数必须增加 1
            connectionList[index] = newConnection;
        }
        // 能走到这里，肯定是能返回一个连接的，要么是原本就空闲的连接，要么就是新创建出来的连接
        connectionBitMap[index] = BUSY_VALUE; // 连接状态设置为忙碌
        return connectionList[index];
    }

    // 该方法用于扩容
    private static int grow(){
        Connection[] newConnectionList = new MyConnection[connectionList.length * 2];
        byte[] newConnectionBitMap = new byte[connectionBitMap.length * 2];
        System.arraycopy(connectionList,0,newConnectionList,0,connectionList.length);
        System.arraycopy(connectionBitMap,0,newConnectionBitMap,0,connectionBitMap.length);
        // 初始化新状态数组里新扩容出来的空间的状态
        for(int i = connectionBitMap.length; i < newConnectionBitMap.length; i ++){
            newConnectionBitMap[i] = -1;
        }
        // 神不知鬼不觉扩容
        connectionList = newConnectionList;
        connectionBitMap = newConnectionBitMap;
        return getNullIndex(); // 返回最新的空置的连接的下标
    }

    public static Connection getConnection(){
        int freeIndex = getFreeIndex(); // 获取空闲连接的下标
        if(freeIndex == Integer.MAX_VALUE){ // 如果找不到空闲连接的下标
            // 找不到空闲的连接，要判断是否有空置的连接
            int nullIndex = getNullIndex();
            if(nullIndex == Integer.MAX_VALUE){ // 如果没有空置的连接了
                // 进行判断是否能进行扩容
                if(total < ReadDBConfig.getInstance().getConnectionCount("maxConnectionSize","10")){
                    nullIndex = grow(); // 扩容后返回新的空置连接的下标
                }else{ // 没有空闲，空置，且不能扩容了，返回 null
                    return null;
                }
            }
            // 没有空闲的连接，但有空置的连接
            return getDistribute(nullIndex);
        }
        // 有空闲的连接，才能走到这里
        return getDistribute(freeIndex);
    }

    // 释放连接
    public static void freeConnection(Connection theConnection) throws FreeConnectionException{
        for(int index = 0; index < connectionList.length; index ++){  // 找出要释放的连接对应的下标
            // 之所以要 connectionBitMap[index] == BUSY_VALUE
            // 是因为防止无故释放/重复释放，即如果不这么写，那空闲的也可以释放，出现二次释放的情况
            if(connectionList[index] == theConnection && connectionBitMap[index] == BUSY_VALUE){
                connectionBitMap[index] = FREE_VALUE;  // 释放连接(状态设置为FREE_VALUE).
                System.out.println("操作完成,释放连接成功");
                return;
            }
        }
        // 没找到，释放异常
        throw new FreeConnectionException("释放连接异常");
    }
}
