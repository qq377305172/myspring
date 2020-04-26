package com.jing.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * @author cj
 * @date 2020/4/21 14:03
 */
public class RadixSort {
    public static void main(String[] args) {
        int[] arr = {23, 6, 189, 45, 9, 287, 56, 789, 65, 10};
        me(arr);
        System.out.println(Arrays.toString(arr));
    }

    private static void me(int[] arr) {
        //存数组里的最大元素
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        //最大数的位数
        int maxLength = String.valueOf(max).length();
        //用于临时存储数据的二维数组
        int[][] tempArr = new int[10][arr.length];
        List<Queue<Integer>> queue = new ArrayList<>(10);
        //用于记录在tempArr的相应的数组种存放的元素数量
        int[] counts = new int[10];
        //比较最大长度的元素的位数次
        for (int i = 0, n = 1; i < maxLength; i++, n *= 10) {
            for (int j = 0; j < arr.length; j++) {
                //计算余数
                int remainder = arr[j] / n % 10;
                //把当前遍历的数字放到指定的数组的数组种
                tempArr[remainder][counts[remainder]] = arr[j];
                counts[remainder]++;
            }
            //记录取的元素需要放的位置
            int index = 0;
            //把数字取出来
            for (int j = 0; j < counts.length; j++) {
                if (counts[j] != 0) {
                    //循环取
                    for (int k = 0; k < counts[j]; k++) {
                        arr[index] = tempArr[j][k];
                        index++;
                    }
                    counts[j] = 0;
                }
            }
        }
    }
}
