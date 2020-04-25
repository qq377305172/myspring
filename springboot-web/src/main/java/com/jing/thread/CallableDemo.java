package com.jing.thread;




import java.util.concurrent.*;

/**
 * @time 2020/4/14 20:48
 * @author Admin
 */
class MyThread implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
}

class MyThread2 implements Callable<Integer> {

    @Override
    public Integer call() {
        return 1;
    }
}

public class CallableDemo {
    public static void main(String[] args) {
//        String s = IdUtil
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        Future<Integer> submit = service.submit(new MyThread2());
//        try {
//            System.out.println(submit.get());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        service.submit(new MyThread());
//        service.shutdown();
    }
}
