package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/23 16:21
 */
public class CopyArr {
    public static void main(String[] args) {
        int num = 3;
        sub(num);
    }

    private static void sub(int num) {
        int[] arr1 = {1, 2, 3, 4, 5};
        int[] arr2 = new int[arr1.length - 1];
        int count = 0;
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] == num) {
                continue;
            }
            arr2[count] = arr1[i];
            count++;
        }
        System.out.println(Arrays.toString(arr2));
    }

    private static void add() {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {4, 5, 6};
        int[] arrNew = new int[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            arrNew[i] = arr1[i];
        }
        for (int i = 0; i < arr2.length; i++) {
            arrNew[i + arr1.length] = arr2[i];
        }
        System.out.println(Arrays.toString(arrNew));
    }
}
