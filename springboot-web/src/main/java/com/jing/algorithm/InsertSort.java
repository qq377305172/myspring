package com.jing.algorithm;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author cj
 * @date 2020/4/20 16:46
 */
public class InsertSort {
    public static void main(String[] args) {
        int[] arr = {5, 1, 6, 8, 3, 4, 9, 2, 6, 5};
        me(arr);
        System.out.println(Arrays.toString(arr));
    }

    public static void me(int[] arr) {
        try {
            TimeUnit.MINUTES.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //遍历所有数字
        for (int i = 1; i < arr.length; i++) {
            //如果当前数字比后一个大
            if (arr[i - 1] > arr[i]) {
                //把当前遍历的数字存起来
                int temp = arr[i];
                int j;
                //遍历当前数字前面所有数字
                for (j = i - 1; j >= 0 && temp < arr[j]; j--) {
                    //把前一个数字赋给后一个数字
                    arr[j + 1] = arr[j];
                }
                //把临时变量(外层for循环的当前元素)赋给不满足条件的后一个元素
                arr[j + 1] = temp;
            }
        }
    }
}
