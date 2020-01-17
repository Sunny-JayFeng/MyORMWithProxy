package service;

import dao.WuXiaDao;
import sqlsession.DaoProxy;

import java.util.LinkedList;

public class WuXiaService {

    private static WuXiaDao wuxiaDao = (WuXiaDao) DaoProxy.getInstance(WuXiaDao.class);

    public static long insert(String school_name, String address, String headmaster){
        return wuxiaDao.insert(school_name, address, headmaster);
    }

    public static int delete(Integer num){
        return wuxiaDao.delete(num);
    }

    public static int update(String headmaster, String school_name){
        return wuxiaDao.update(headmaster, school_name);
    }

    public static LinkedList select(){
        return wuxiaDao.select();
    }
}
