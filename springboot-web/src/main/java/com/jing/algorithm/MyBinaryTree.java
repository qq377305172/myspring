package com.jing.algorithm;

import lombok.ToString;

/**
 * @author cj
 * @date 2020/4/24 13:52
 */
@ToString
class BinaryTree {
    //节点的权
    private int data;
    private BinaryTree left;
    private BinaryTree right;

    public BinaryTree(int data) {
        this.data = data;
    }

    public void setLeft(BinaryTree binaryTree) {
        this.left = binaryTree;
    }

    public void setRight(BinaryTree binaryTree) {
        this.right = binaryTree;
    }

    public void frontShow() {
        System.out.print(this.data);
        if (null != left) {
            left.frontShow();
        }
        if (null != right) {
            right.frontShow();
        }
    }

    public void midShow() {
        if (null != left) {
            left.midShow();
        }
        System.out.print(this.data);
        if (null != right) {
            right.midShow();
        }
    }

    public void afterShow() {
        if (null != left) {
            left.afterShow();
        }
        if (null != right) {
            right.afterShow();
        }
        System.out.print(this.data);
    }

    public boolean frontSearch(int num) {
        if (this.data == num) {
            return true;
        } else {
            if (this.left != null) {
                if (left.data == num) {
                    return true;
                } else {
                    left.frontSearch(num);
                }
            }
            if (this.right != null) {
                if (right.data == num) {
                    return true;
                } else {
                    right.frontSearch(num);
                }
            }
        }
        return false;
    }
}

public class MyBinaryTree {
    public static void main(String[] args) {
        BinaryTree binaryTree1 = new BinaryTree(1);
        BinaryTree binaryTree2 = new BinaryTree(2);
        BinaryTree binaryTree3 = new BinaryTree(3);
        BinaryTree binaryTree4 = new BinaryTree(4);
        BinaryTree binaryTree5 = new BinaryTree(5);
        BinaryTree binaryTree6 = new BinaryTree(6);
        BinaryTree binaryTree7 = new BinaryTree(7);
        binaryTree1.setLeft(binaryTree2);
        binaryTree1.setRight(binaryTree3);

        binaryTree2.setLeft(binaryTree4);
        binaryTree2.setRight(binaryTree5);

        binaryTree3.setLeft(binaryTree6);
        binaryTree3.setRight(binaryTree7);
        System.out.print("前");
        binaryTree1.frontShow();
        System.out.println();
        System.out.print("中");
        binaryTree1.midShow();
        System.out.println();
        System.out.print("后");
        binaryTree1.afterShow();
        //前序查找
        boolean res = binaryTree1.frontSearch(5);
        System.out.println(res);
    }
}
