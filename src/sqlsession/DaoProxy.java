package sqlsession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

// 动态代理类
public class DaoProxy {

    public static Object getInstance(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MethodHandler());
    }

    private static class MethodHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws
                                                                            InvocationTargetException,
                                                                            IllegalAccessException,
                                                                            SQLException,
                                                                            NoSuchFieldException,
                                                                            InstantiationException {
            if(Object.class.equals(method.getDeclaringClass())){

                return method.invoke(this,args);
            }
            SQL sql = method.getAnnotation(SQL.class);
            return execute(sql, args);
        }

        private static Object execute(SQL sql, Object[] args) throws
                                                                                SQLException,
                                                                                IllegalAccessException,
                                                                                NoSuchFieldException,
                                                                                InstantiationException {
            if(sql != null){
                switch(sql.type()){
                    case INSERT:
                            return SQLSessionFactory.insert(sql.sqlSentence(),args);
                    case DELETE:
                            return SQLSessionFactory.update(sql.sqlSentence(),args);
                    case UPDATE:
                            return SQLSessionFactory.update(sql.sqlSentence(),args);
                    case SELECT:
                            return SQLSessionFactory.select(sql.sqlSentence(),args,sql.resultType());
                }
            }
            return null;
        }
    }
}
