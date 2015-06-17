/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import scala.Tuple3;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 *
 * @author Dariusz Hudziak
 */
public class HashTagMapper implements PairFlatMapFunction<Status, Tuple3<String,String,String>, Integer> {

    @Override
    public Iterable<Tuple2<Tuple3<String, String, String>, Integer>> call(Status t) throws Exception {
       List<Object[]> keyword=null;
       DatabaseHelper dh=null;
       
       try {
           dh = DatabaseHelper.openDB();
           keyword = dh.listKeywords();
       } catch(SQLException e) {
           e.printStackTrace();
       } finally {
           if(dh!=null) dh.close();
       }
       
       Iterator<Object[]> keyIter = keyword.iterator();
       while(keyIter.hasNext()) {
           Object[] o = keyIter.next();
           if(!t.getText().contains((String)o[0])){
               keyIter.remove();
           }
       }
       
       List<Tuple2<Tuple3<String,String,String>,Integer>> list = new LinkedList<>();
       
       for(HashtagEntity ha : t.getHashtagEntities()) {
           for(Object[] o : keyword) {
             list.add(new Tuple2(new Tuple3(ha.getText(),o[1],o[0]),Integer.valueOf(1)));
           }
       }
       
       return list;
    }
    
}
