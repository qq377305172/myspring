package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/20 16:46
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {5, 1, 6, 8, 3, 4, 9, 2, 6, 5};
        me(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));

    }

    public void me2(int[] arr, int start, int end) {
        if (start < end) {
            int low = start;
            int high = end;
            int standard = arr[start];
            while (low < high) {
                while (low < high && arr[high] > low) {
                    high--;
                }
                arr[low] = arr[high];
                while (low < high && arr[low] < high) {
                    low++;
                }
                arr[high] = arr[low];
            }
            arr[low] = standard;
            me(arr, 0, low);
            me(arr, low + 1, high);
        }

    }


    public static void me(int[] arr, int start, int end) {
        if (end > start) {
            //把数组中第一个元素作为比较标准
            int standard = arr[start];
            //记录需要排序的下标
            int low = start;
            int high = end;
            //循环找比标准数大和比标准数小的数,如果开始位置和结束位置发生了重合则结束循环
            while (low < high) {
                //右边的数比标准数大
                while (low < high && standard <= arr[high]) {
                    high--;
                }
                //右边数比标准数小,使用右边的数字替换左边的数字
                arr[low] = arr[high];
                //左边的数比标准数小
                while (low < high && arr[low] < standard) {
                    low++;
                }
                //左边数比标准数大,使用左边的数替换右边的数
                arr[high] = arr[low];
            }
            //把标准数赋值给低所在位置的元素(此时低所在位置和高所在位置已重合)
            arr[low] = standard;
            //处理所有小的数字
            me(arr, start, low);
            //处理所有大的数字
            me(arr, low + 1, end);
        }
    }
}
