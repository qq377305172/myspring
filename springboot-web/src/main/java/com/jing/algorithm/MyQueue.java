package com.jing.algorithm;

import lombok.Data;
import lombok.ToString;

/**
 * @author cj
 * @date 2020/4/23 18:26
 */
@Data
@ToString
class QueueDemo {
    private int[] elements;

    public QueueDemo() {
        this.elements = new int[0];
    }

    public int getLength() {
        return this.elements.length;
    }

    public void add(int num) {
        int[] arrNew = new int[getLength() + 1];
        for (int i = 0; i < getLength(); i++) {
            arrNew[i] = this.elements[i];
        }
        arrNew[arrNew.length - 1] = num;
        this.elements = arrNew;
    }

    public int poll() {
        int[] arrNew = new int[getLength() - 1];
        int res = this.elements[0];
        for (int i = 0; i < arrNew.length; i++) {
            arrNew[i] = this.elements[i + 1];
        }
        this.elements = arrNew;
        return res;
    }
}

public class MyQueue {
    public static void main(String[] args) {
        QueueDemo queueDemo = new QueueDemo();
        queueDemo.add(0);
        queueDemo.add(1);
        queueDemo.add(2);
        System.out.println(queueDemo.poll());
        System.out.println(queueDemo.poll());
        System.out.println(queueDemo.poll());
        System.out.println(queueDemo);
    }
}
