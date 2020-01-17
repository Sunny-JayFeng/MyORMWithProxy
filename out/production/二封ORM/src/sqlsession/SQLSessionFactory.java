package sqlsession;

import util.ConnectionPool;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SQLSessionFactory {

    // 增
    public static long insert(String sqlSentence, Object[] args) throws SQLException{
        Connection connection = ConnectionPool.getConnection(); // 从连接池中获取一个连接
        PreparedStatement pstat = connection.prepareStatement(sqlSentence,Statement.RETURN_GENERATED_KEYS);
        if(args != null) setParamter(pstat, args);  // 如果有参数(需要设置问号的值)，那就调用方法，设置问号的值
        pstat.executeUpdate(); // 执行SQL语句
        ResultSet rs = pstat.getGeneratedKeys(); // 新增的主键的值

        // 为了 finally 关闭， 所以包一层什么都不捕获的 try
        try {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } finally{
            rs.close();
            pstat.close();
            connection.close();
        }
        return -1;

    }
    // 这个方法用于对传进来的SQL语句，中问号的赋值操作
    private static void setParamter(PreparedStatement pstat, Object[] args) throws SQLException {
        for(int i = 0; i < args.length; i ++){
            pstat.setObject(i + 1, args[i]);
        }
    }

    // 删,改
    public static int update(String sqlSentence, Object[] args) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement pstat = connection.prepareStatement(sqlSentence);
        if(args != null) setParamter(pstat, args);  // 如果有参数(需要设置问号的值)，那就调用方法，设置问号的值
        // 为了 finally 关闭， 所以包一层什么都不捕获的 try
        try {
            return pstat.executeUpdate(); // 返回修改的行数(第几行)
        } finally{
            pstat.close();
            connection.close();
        }
    }

    // 查  返回的是一个集合
    public static LinkedList select(String sqlSentence, Object[] args, Class resultType) throws
                                                                                            SQLException,
                                                                                            InstantiationException,
                                                                                            IllegalAccessException,
                                                                                            NoSuchFieldException{
        LinkedList collection = new LinkedList();
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement pstat = connection.prepareStatement(sqlSentence);
        if(args != null) setParamter(pstat, args);  // 如果有参数(需要设置问号的值)，那就调用方法，设置问号的值
        ResultSet rs = pstat.executeQuery();  // 执行查询操作
        while(rs.next()){
            // 在这里需要调用方法，把查询得到的每一条记录包装成一个对象
            Object theObject = returnAObject(rs, resultType);
            collection.add(theObject);
        }
        return collection;
    }

    // 该方法，用来将查询到的结果的每一条记录都包装成一个对象
    private static Object returnAObject(ResultSet rs, Class resultType) throws
                                                                            InstantiationException,
                                                                            IllegalAccessException,
                                                                            SQLException,
                                                                            NoSuchFieldException{
        Object theObject = resultType.newInstance();
        for(int i = 0; i < rs.getMetaData().getColumnCount(); i ++){
            String columnName = rs.getMetaData().getColumnName(i + 1);
            setFieldValue(theObject, rs, columnName, resultType);
        }
        return theObject;
    }

    // 该方法用于对属性进行赋值操作
    private static void setFieldValue(Object theObject, ResultSet rs, String columnName, Class resultType) throws
                                                                                                            NoSuchFieldException,
                                                                                                            SQLException,
                                                                                                            IllegalAccessException{
        String fieldName = getFieldName(columnName);
        Field field = resultType.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(theObject, rs.getObject(columnName));
    }

    // 该方法，用于得到查询出来的记录的列民对应的对象的属性名
    private static String getFieldName(String colName){
        colName = colName.toLowerCase(); // 先全部转化为小写
        // 正则匹配
        Pattern pattern = Pattern.compile("_(\\w)"); // 找寻字符串里面，带有 下划线，并且下划线后面跟着字母的。如：_A
        Matcher matcher = pattern.matcher(colName);
        StringBuffer fieldName = new StringBuffer("");
        while(matcher.find()){ // 如果找到匹配的
            matcher.appendReplacement(fieldName,matcher.group(1).toUpperCase());
        }
        matcher.appendTail(fieldName);
        return fieldName.toString();
    }
}
