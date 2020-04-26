package com.jing.algorithm;

/**
 * @author cj
 * @date 2020/4/20 16:23
 */
public class Hanoi {
    public static void main(String[] args) {
        me(3, 'A', 'B', 'C');
    }

    /**
     * @param n    盘子总数
     * @param left 开始柱
     * @param mid  中间柱
     * @param right 目标柱
     *             无论有多少个盘子,都认为只有两个:上面所有盘子和最下面的一个盘子
     */
    public static void me(int n, char left, char mid, char right) {
        if (n == 1) {
            System.out.println("第" + n + "个盘子从" + left + "移到" + right);
        } else {
            //移动上面所有的盘子到中间位置
            me(n - 1, left, right, mid);
            //移动下面的盘子
            System.out.println("第" + n + "个盘子从" + left + "移到" + right);
            me(n - 1, mid, left, right);
        }

    }
}
