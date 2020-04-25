package com.jing.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cj
 * @date 2020/4/13 14:17
 */
public class CASDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        boolean b = atomicInteger.compareAndSet(5, 1);
        System.out.println(atomicInteger.get());
    }
}
