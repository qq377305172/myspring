package com.jing.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cj
 * @date 2020/4/14 17:42
 */
class ShareResource {
    private int num = 1;
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print5() {
        lock.lock();
        try {
            while (num != 1) {
                try {
                    condition1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " " + num);
            }
            num = 2;
            condition2.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print10() {
        lock.lock();
        try {
            while (num != 2) {
                try {
                    condition2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " " + num);
            }
            num = 3;
            condition3.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print15() {
        lock.lock();
        try {
            while (num != 3) {
                try {
                    condition3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName() + " " + num);
            }
            num = 1;
            condition1.signal();
        } finally {
            lock.unlock();
        }
    }
}

public class SyncAndReentrantLockDemo {
    public static void main(String[] args) {
        ShareResource resource = new ShareResource();
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(resource::print5);
        service.submit(resource::print10);
        service.submit(resource::print15);
        service.shutdown();
    }

}
