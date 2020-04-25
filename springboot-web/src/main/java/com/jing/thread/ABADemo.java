package jing.chao.thread;

import java.lang.ref.Reference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author cj
 * @date 2020/4/13 16:05
 */
public class ABADemo {
    static AtomicReference<Integer> atomicReference = new AtomicReference<Integer>(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<Integer>(100, 1);

    public static void main(String[] args) {
        //ABA问题
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        });
        service.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicReference.compareAndSet(100, 2020);
            System.out.println(atomicReference.get());
        });
        service.shutdown();
        //ABA问题解决
        ExecutorService service2 = Executors.newFixedThreadPool(2);
        service2.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int stamp = atomicStampedReference.getStamp();
            boolean b = atomicStampedReference.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + " " + b + " " + " " + atomicStampedReference.getReference() + " " + stamp);
            int stamp2 = atomicStampedReference.getStamp();
            boolean b1 = atomicStampedReference.compareAndSet(101, 100, stamp2, stamp2 + 1);
            System.out.println(Thread.currentThread().getName() + " " + b1 + " " + " " + atomicStampedReference.getReference() + " " + stamp2);
        });
        service2.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int stamp = atomicStampedReference.getStamp();
            boolean b = atomicStampedReference.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + " " + b + " " + atomicStampedReference.getReference() + " " + stamp);
        });
        service2.shutdown();
    }
}
