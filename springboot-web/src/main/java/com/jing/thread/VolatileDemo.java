package com.jing.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author isi
 * @date 2020/4/13 11:03
 * volatile保证可见性和一致性,不保证原子性
 * 原子性,不可分割,完整性,某个线程在做某个业务时,中间不可以被加塞或分割,需要整体完整,要么同时成功,要么同时失败
 */
class MyData {
    volatile int number;

    public void addPlusPlus() {
        number++;
    }
    AtomicInteger atomicInteger = new AtomicInteger();
    public void addAtomicInteger(){
        atomicInteger.getAndIncrement();
    }
}

public class VolatileDemo {
    public static void main(String[] args) {
        MyData myData = new MyData();
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            service.submit(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.addAtomicInteger();
                }
            });
        }
        service.shutdown();
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(myData.atomicInteger);
    }
}
