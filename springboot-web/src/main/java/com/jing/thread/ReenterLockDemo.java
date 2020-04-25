package com.jing.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cj
 * @date 2020/4/14 12:41
 * 可重入锁,也叫递归锁
 * 同一个线程,外层方法获得锁后,内层的递归方法仍然能够获得该锁的代码
 * 在同一个线程的外层获得锁的时候,在进入内层的方法会自动获取锁
 * <p>
 * 也即是说,线程可以进入任何一个它已经拥有的锁所同步着的代码块
 */
class phone implements Runnable{
    public synchronized void sendSMS() {
        System.out.println(Thread.currentThread().getName() + " 发短信");
        sendEmail();
    }

    public synchronized void sendEmail() {
        System.out.println(Thread.currentThread().getName() + " 发邮件");
    }

    Lock lock = new ReentrantLock();

    public void get() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " get");
            set();
        } finally {
            lock.unlock();
        }
    }

    private void set() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " set");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        get();
    }
}

public class ReenterLockDemo {
    public static void main(String[] args) {
        phone phone = new phone();
        ExecutorService service = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            service.submit(phone);
//            service.submit(phone::sendSMS);
        }
        service.shutdown();
    }
}
