package com.jing.algorithm;

/**
 * @author cj
 * @date 2020/4/24 14:51
 */
class ArrayBinaryTree {
    private int[] data;

    public ArrayBinaryTree(int[] data) {
        this.data = data;
    }

    public void frontShow() {
        frontShow(0);
    }

    private void frontShow(int index) {
        if (this.data == null || this.data.length == 0) {
            return;
        }
        System.out.print(data[index]);
        if (2 * index + 1 < data.length) {
            frontShow(2 * index + 1);
        }
        if (2 * index + 2 < data.length) {
            frontShow(2 * index + 2);
        }
    }
}

public class MyArrayBinaryTree {
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        ArrayBinaryTree tree = new ArrayBinaryTree(data);
        tree.frontShow();
    }
}
