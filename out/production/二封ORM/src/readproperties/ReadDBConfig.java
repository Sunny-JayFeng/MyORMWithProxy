package readproperties;

import java.util.Properties;
import java.io.InputStream;
import java.io. IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ReadDBConfig {

    private String driver;
    private String url;
    private String user;
    private String password;
    private Properties properties;

    // 单例模式  --- 双重检测模式
    private ReadDBConfig(){}  // 第一步，私有化构造方法
    private static volatile ReadDBConfig readDBConfig; // 第二部，私有的静态的当前类的属性
    public static ReadDBConfig getInstance(){  // 静态的公有的方法，返回当前类的对象
        if(readDBConfig == null){
            synchronized (ReadDBConfig.class){
                if(readDBConfig == null){
                    readDBConfig = new ReadDBConfig();
                }
            }
        }
        return readDBConfig;
    }


    {
        InputStream inputStream = null;
        try {
            properties = new Properties();
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbcconfig.properties");
            properties.load(inputStream);
            loadMessage(); // 执行方法，加载配置文件中的信息
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try{
                    inputStream.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    // 该方法用于加载配置文件中的信息
    private void loadMessage(){
        this.driver = properties.getProperty("driver");
        this.url = properties.getProperty("url");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
    }

    // 该方法，返回一个driver字符串，用于加载驱动类
    public String getDriver(){
        return this.driver;
    }
    // 该方法，向外部返回一个Connection对象
    public Connection getURLConnection() throws SQLException{
        return DriverManager.getConnection(url,user,password);
    }

    // 该方法，向外部返回最大/最小连接数
    public int getConnectionCount(String size){
        return Integer.parseInt(properties.getProperty(size));
    }
    public int getConnectionCount(String size,String defaultValue){
        return Integer.parseInt(properties.getProperty(size, defaultValue));
    }





}
