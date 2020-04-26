package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/21 11:55
 */
public class SelectSort {
    public static void main(String[] args) {
        int[] arr = {9, 4, 1, 5, 6, 4, 7, 2, 3, 10};
        me(arr);
        System.out.println(Arrays.toString(arr));
    }

    private static void me(int[] arr) {
        int temp;
        //遍历所有数
        for (int i = 0; i < arr.length; i++) {
            //记录最小的数的位置
            int minIndex = i;
            //把当前遍历的数一次和后面的数进行比较,记录下最小的数的下标
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            //如果最小的数和当前遍历的数下标不一致,说明下标为minIndex的数比当前遍历的数小
            if (i != minIndex) {
                temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }
    }
}
