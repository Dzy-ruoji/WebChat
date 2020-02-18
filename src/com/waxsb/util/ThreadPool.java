package com.waxsb.util;/*
package com.com.waxsb.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

*/
/*
* 线程池的使用步骤：
*   1.使用线程池的工厂类Executors里边提供的静态方法newFixedThreadPool生产一个指定线程数量的线程池
*   2.创建一个类，实现Runnable结构，重写run方法，设置线程任务
*   3.调用ExecutorService中的方法submit，传递线程任务（实现类），开启线程，执行run方法
*   4.调用ExecutorService中的方法shutdown销毁线程池（不建议执行）
* *//*

public class ThreadPool {
    public static void main(String[] args){
        // 1.使用线程池的工厂类Executors里边提供的静态方法newFixedThreadPool生产一个指定线程数量的线程池
        ExecutorService es = Executors.newFixedThreadPool(2);//2个线程
        //3.调用ExecutorService中的方法submit，传递线程任务（实现类），开启线程，执行run方法
        es.submit(new RunnableImpl());
        es.submit(new RunnableImpl());
        es.submit(new RunnableImpl());
    }

}
*/
