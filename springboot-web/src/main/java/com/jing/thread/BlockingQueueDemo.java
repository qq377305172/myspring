package com.jing.thread;


import java.util.concurrent.*;

/**
 * @author cj
 * @date 2020/4/14 16:31
 * 阻塞队列:为空时获取元素将会阻塞,为满时插入元素将会阻塞
 * ArrayBlockingQueue: 基于数组的有界阻塞队列.先进先出
 * LinkedBlockingQueue: 基于链表的的阻塞队列,先进先出,吞吐量高于ArrayBlockingQueue
 */
public class BlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            service.submit(() -> {
                try {
                    blockingQueue.put("" + finalI);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        TimeUnit.SECONDS.sleep(1);
        for (int i = 0; i < 5; i++) {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(blockingQueue);
            service.submit((Runnable) blockingQueue::poll);
        }
        service.shutdown();
    }

    private static void m1() throws InterruptedException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5);
        ExecutorService service = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            service.submit(() -> {
                try {
                    blockingQueue.put(String.valueOf(finalI));
//                    arrayBlockingQueue.offer(String.valueOf(finalI));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        TimeUnit.SECONDS.sleep(2);
        blockingQueue.poll();
        service.shutdown();
        System.out.println(blockingQueue);
    }
}
