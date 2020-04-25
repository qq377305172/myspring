package com.jing.thread;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cj
 * @date 2020/4/14 17:51
 */
@RequiredArgsConstructor(staticName = "blockingQueue")
class MyResource {
    private volatile boolean flag = true;
    private AtomicInteger atomicInteger = new AtomicInteger();
    private BlockingQueue<Integer> blockingQueue = null;

    public MyResource(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public void myProd() throws InterruptedException {
        int data;
        boolean retValue;
        while (this.flag) {
            data = this.atomicInteger.incrementAndGet();
            retValue = this.blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retValue) {
                System.out.println(Thread.currentThread().getName() + " 生产成功 " + data);
            } else {
                System.out.println(Thread.currentThread().getName() + " 生产失败 " + data);
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("生产结束");
    }

    public void myConsumer() throws InterruptedException {
        Integer data;
        while (this.flag) {
            data = this.blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (null != data) {
                System.out.println(Thread.currentThread().getName() + " 消费成功 " + data);
            } else {
                this.flag = false;
                System.out.println("消费结束");
                return;
            }
        }
    }

    public void stop() {
        this.flag = false;
    }
}

public class ProdConsumerBlockQueueDemo {
    public static void main(String[] args) {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> {
            try {
                myResource.myProd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.submit(() -> {
            try {
                myResource.myConsumer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.shutdown();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myResource.stop();
    }
}
