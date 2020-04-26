package com.jing.algorithm;

/**
 * @author cj
 * @date 2020/4/20 16:07
 */
public class Fibonacci {
    public static void main(String[] args) {
        //1 1 2 3 5 8 13 21 34 55 89
        System.out.println(me(11));

    }

    public static int me(int index) {
        if (index == 1 || index == 2) {
            return 1;
        } else {
            return me(index - 1) + me(index - 2);
        }
    }
}
