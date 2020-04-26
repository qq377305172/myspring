package com.jing.algorithm;

/**
 * @author cj
 * @date 2020/4/23 16:48
 */
class SingletonDemo {
    private static SingletonDemo INSTANCE = null;

    private SingletonDemo() {

    }

    public static SingletonDemo getSingletonDemo() {
        if (INSTANCE == null) {
            synchronized (SingletonDemo.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SingletonDemo();
                }
            }
        }
        return INSTANCE;
    }
}

public class MySingleton {
    public static void main(String[] args) {
        System.out.println(SingletonDemo.getSingletonDemo() == SingletonDemo.getSingletonDemo());
    }

}
