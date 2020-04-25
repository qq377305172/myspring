package com.jing.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author cj
 * @date 2020/4/14 15:33
 * 多个线程同时读一个资源时没问题,所以为了满足并发量,读取共享资源时可以同时进行
 * 但是
 * 多个线程不可以同时去写共享资源
 */
class MyCache {//资源类
    private volatile Map<String, Object> map = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void put(String key, Object value) throws InterruptedException {

        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " put start");
            TimeUnit.MILLISECONDS.sleep(300);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + " put end");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Object get(String key) throws InterruptedException {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " get start ");
            Object o = map.get(key);
            TimeUnit.MILLISECONDS.sleep(300);
            System.out.println(Thread.currentThread().getName() + " get end " + o);
            return o;
        } finally {
            lock.readLock().unlock();
        }
    }
}

public class ReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        MyCache myCache = new MyCache();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                try {
                    myCache.put(String.valueOf(finalI), finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
//        TimeUnit.SECONDS.sleep(1);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                try {
                    Object o = myCache.get(String.valueOf(finalI));
                    System.out.println(o);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
    }
}
