package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/20 16:46
 */
public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = {5, 1, 6, 8, 3, 4, 9, 2, 6, 5};
        me(arr);
    }

    public static void me(int[] arr) {
        int temp;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
