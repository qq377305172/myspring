package com.jing.algorithm;

import cn.hutool.core.util.ArrayUtil;

/**
 * @author cj
 * @date 2020/4/20 14:19
 */
public class BinarySearch {
    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] arr2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        System.out.println(me(arr, 7));

        int target = -1;


        System.out.println(ArrayUtil.indexOf(arr, target));
    }

    private static int me2(int[] arr, int target) {
        int begin = 0;
        int end = arr.length - 1;
        int mid = (begin + end) / 2;
        for (; ; ) {
            if (arr[mid] == target) {
                return arr[mid];
            } else if (target > arr[mid]) {
                begin = mid + 1;
            } else if (target < arr[mid]) {
                end = mid - 1;
            }
            mid = (begin + end) / 2;
            if (begin > end) {
                return -1;
            }
        }
    }

    private static int me(int[] arr, int target) {
        //开始的查找位置0
        int begin = 0;
        //结束的查找位置数组最后一个元素
        int end = arr.length - 1;
        //查找的中间元素
        int mid = (begin + end) / 2;
        for (; ; ) {
            //目标元素等于数组的中间元素则查找结束
            if (target == arr[mid]) {
                return arr[mid];
            } else {
                //要查找的元素比数组中间元素小
                if (arr[mid] > target) {
                    //结束为止更新为数组中间位置的前一个位置
                    end = mid - 1;
                    //要查找的元素比数组中间元素大
                } else {
                    //结束为止更新为数组中间位置的后一个位置
                    begin = mid + 1;
                }
                //更新中间位置
                mid = (begin + end) / 2;
            }
            //数组中无该元素
            if (begin > end) {
                return -1;
            }
        }
    }
}
