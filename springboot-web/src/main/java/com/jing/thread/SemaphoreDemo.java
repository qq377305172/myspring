package com.jing.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author cj
 * @date 2020/4/14 16:25
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(finalI + Thread.currentThread().getName() + " in");
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(finalI + Thread.currentThread().getName() + " out");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            });
        }
        service.shutdown();

    }
}
