package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/21 11:22
 */
public class ShellSort {
    public static void main(String[] args) {
        int[] arr = {9, 4, 1, 5, 6, 4, 7, 2, 3, 10};
        me(arr);
        System.out.println(Arrays.toString(arr));

    }

    private static void me(int[] arr) {
        int temp;
        //遍历所有步长
        for (int d = arr.length / 2; d > 0; d /= 2) {
            //遍历所有元素
            for (int i = d; i < arr.length; i++) {
                //遍历本组中所有元素
                for (int j = i - d; j >= 0; j -= d) {
                    if (arr[j] > arr[j + d]) {
                        temp = arr[j];
                        arr[j] = arr[j + d];
                        arr[j + d] = temp;
                    }
                }
            }
        }
    }
}
