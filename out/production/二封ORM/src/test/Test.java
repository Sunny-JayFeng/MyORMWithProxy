package test;

import service.WuXiaService;
import sqlsession.SQLSessionFactory;
import domain.WuXia;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

public class Test {

    public static void main(String[] args){

//        long result = WuXiaService.insert("A","ABC","C");
//        System.out.println(result);

        int result = WuXiaService.delete(17);
        System.out.println(result);

//        WuXiaService.insert("A","ABC","C");
//        int result = WuXiaService.update("D","A");
//        System.out.println(result);

//        LinkedList result = WuXiaService.select();
//        Iterator it = result.iterator();
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }
    }
}