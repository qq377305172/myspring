package com.jing.algorithm;

import lombok.Data;
import lombok.ToString;

/**
 * @author cj
 * @date 2020/4/23 17:40
 */
@Data
@ToString
class StackDemo {
    private int[] elements;

    public StackDemo() {
        this.elements = new int[0];
    }

    public int getLength() {
        return this.elements.length;
    }

    public void push(int num) {
        int[] arrNew = new int[this.elements.length + 1];
        for (int i = 0; i < getLength(); i++) {
            arrNew[i] = this.elements[i];
        }
        arrNew[getLength()] = num;
        this.elements = arrNew;
    }

    public int pop() {
        if (getLength() == 0) throw new ArrayIndexOutOfBoundsException();
        int res = this.elements[getLength() - 1];
        int[] arrNew = new int[this.elements.length - 1];
        for (int i = 0; i < arrNew.length; i++) {
            arrNew[i] = this.elements[i];
        }
        this.elements = arrNew;
        return res;
    }
}

public class MyStack {
    public static void main(String[] args) {
        StackDemo stackDemo = new StackDemo();
        stackDemo.push(0);
        stackDemo.push(1);
        stackDemo.push(2);
        stackDemo.push(3);
        System.out.println(stackDemo.pop());
        System.out.println(stackDemo.pop());
        System.out.println(stackDemo.pop());
    }
}
