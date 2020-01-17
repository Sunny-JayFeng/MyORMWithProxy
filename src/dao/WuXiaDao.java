package dao;

import domain.WuXia;
import sqlsession.SQL;
import sqlsession.SQLEnum;

import java.util.LinkedList;

public interface WuXiaDao {

    @SQL(sqlSentence = "INSERT INTO wuxia(school_name,address,headmaster) VALUES(?,?,?)", type = SQLEnum.INSERT, resultType = WuXia.class)
    public long insert(String school_name, String address, String headmaster);

    @SQL(sqlSentence = "DELETE FROM wuxia WHERE num = ?", type = SQLEnum.DELETE, resultType = WuXia.class)
    public int delete(Integer num);

    @SQL(sqlSentence = "UPDATE wuxia SET headmaster = ? WHERE school_name = ?", type = SQLEnum.UPDATE, resultType = WuXia.class)
    public int update(String headmaster, String school_name);

    @SQL(sqlSentence = "SELECT num, school_name, address, headmaster FROM wuxia", type = SQLEnum.SELECT, resultType = WuXia.class)
    public LinkedList select();

}
