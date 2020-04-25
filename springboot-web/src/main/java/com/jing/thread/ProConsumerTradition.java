package com.jing.thread;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cj
 * @date 2020/4/14 17:13
 */
class ShareData {
    private int num = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment() {
        lock.lock();
        try {
            while (num != 0) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        num++;
        this.condition.signal();
        System.out.println(num);
    }

    public void decrement() {
        lock.lock();
        try {
            while (num != 1) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        num--;
        this.condition.signal();
        System.out.println(num);
    }
}

public class ProConsumerTradition {
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            service.submit(() -> {
                shareData.increment();
                shareData.decrement();
            });
        }
        service.shutdown();
    }
}
