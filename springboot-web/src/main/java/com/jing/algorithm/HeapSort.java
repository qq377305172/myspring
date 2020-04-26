package com.jing.algorithm;

import java.util.Arrays;

/**
 * @author cj
 * @date 2020/4/24 15:10
 */
public class HeapSort {
    public static void main(String[] args) {
        int[] arr = {9, 6, 8, 7, 0, 1, 10, 4, 2};
        //开始位置是最后一个非叶子节点,即最后一个节点的父节点
        int start = (arr.length - 1) / 2;
        //结束为止,数组长度-1
        for (int i = start; i >= 0; i--) {
            maxHeap(arr, arr.length, start);
        }
        System.out.println(Arrays.toString(arr));
    }

    public static void maxHeap(int[] arr, int size, int index) {
        //左子节点
        int leftNode = 2 * index + 1;
        //右子节点
        int rightNode = 2 * index + 2;
        int max = index;
        //和两个节点分别对比,找出最大的节点
        if (leftNode < size) {
            if (arr[leftNode] > arr[rightNode]) {
                max = leftNode;
            } else {
                max = rightNode;
            }
        }
        //交换位置
        if (max != index) {
            int temp = arr[index];
            arr[index] = arr[max];
            arr[max] = temp;
            //交换位置后,可能会破坏之前排好的堆,需要重新调整
            maxHeap(arr, size, max);
        }
    }

}

