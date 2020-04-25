package com.jing.thread;

import cn.hutool.core.util.IdUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author cj
 * @date 2020/4/14 10:22
 */
public class ContainerNotSafeDemo {
    public static void main(String[] args) {
        setNotSafe();
    }

    private static void setNotSafe() {
        Set<String> set = new CopyOnWriteArraySet<>();
//        Set<String> set = new HashSet<>();
        ExecutorService service = Executors.newFixedThreadPool(30);
        for (int i = 0; i < 30; i++) {
            service.submit(() -> {
                set.add(IdUtil.simpleUUID());
                System.out.println(set);
            });
        }
        service.shutdown();
    }
    private static void listNotSafe() {
        //        List<String> list = Collections.synchronizedList(new ArrayList<>());
//        List<String> list = new Vector<>();
        List<String> list = new CopyOnWriteArrayList<>();
//        List<String> list = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(30);
        for (int i = 0; i < 30; i++) {
            service.submit(() -> {
                list.add(IdUtil.simpleUUID());
                System.out.println(list);
            });
        }
        service.shutdown();
    }
}
