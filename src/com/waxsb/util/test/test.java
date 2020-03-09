package com.waxsb.util.test;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.OffsetTime.now;


public class test {
    @Test
    public  void mains() throws ParseException {
      /*  Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);


        new Timer("testTimer").schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("删除两周前的聊天记录");
                java.util.Date date = new java.util.Date();
                Timestamp timeStamp = new Timestamp(date.getTime());//获取当前日期
            }
        }, 1000,2000);
*/
        java.util.Date date = new java.util.Date();          // 获取一个Date对象
        Timestamp timeStamp = new Timestamp(date.getTime());
        System.out.println(timeStamp);
    }
}
