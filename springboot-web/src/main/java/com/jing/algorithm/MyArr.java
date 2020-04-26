package com.jing.algorithm;

import lombok.Data;
import lombok.ToString;

/**
 * @author cj
 * @date 2020/4/23 16:38
 */
@Data
@ToString
class ArrObj {
    private int[] elements;

    public static ArrObj getInstance() {
        return arrObjHolder.INSTANCE;
    }

    private ArrObj() {
        this.elements = new int[0];
    }

    private static class arrObjHolder {
        private static final ArrObj INSTANCE = new ArrObj();
    }

    public int getLength() {
        return this.elements == null ? 0 : this.elements.length;
    }

    public void add(int index, int num) {
        int[] ints = new int[this.elements.length + 1];
        for (int i = 0; i < elements.length; i++) {
            if (i < index) {
                ints[i] = this.elements[i];
            } else {
                ints[i + 1] = this.elements[i];
            }
        }
        ints[index] = num;
        this.elements = ints;
    }
}

public class MyArr {
    public static void main(String[] args) {
        ArrObj instance = ArrObj.getInstance();
        instance.add(0, 0);
        instance.add(0, 1);
        instance.add(0, 2);
        System.out.println(instance);
    }

}
