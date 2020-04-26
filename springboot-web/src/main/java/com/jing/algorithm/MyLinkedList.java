package com.jing.algorithm;

import lombok.ToString;

/**
 * @author cj
 * @date 2020/4/24 9:50
 */
@ToString
class Node {
    private int data;
    private Node next;

    public Node(int data) {
        this.data = data;
    }

    public Node append(Node node) {
        Node currentNode = this;
        while (true) {
            Node nextNode = currentNode.next;
            if (nextNode == null) {
                break;
            }
            currentNode = nextNode;
        }
        currentNode.next = node;
        return this;
    }

    public void insert(Node node) {
        //当前节点的下一个节点
        Node currentNext = this.next;
        node.append(currentNext);
        this.next = node;
    }

    public Node next() {
        return this.next;
    }

    public int getData() {
        return this.data;
    }

    public boolean isLast() {
        return this.next == null;
    }

    /*
    删除下一个节点
     */
    public boolean removeNext() {
        if (null != next) {
            synchronized (Node.class) {
                if (null != next) {
                    this.next = this.next.next;
                    return true;
                }
            }
        }
        return false;
    }
}

class LoopNode {
    private int data;
    private LoopNode next = this;

    public LoopNode(int data) {
        this.data = data;
    }


    public void insert(LoopNode node) {
        //当前节点的下一个节点
        LoopNode currentNext = this.next;
        this.next = node;
        node.next = currentNext;
    }

    public LoopNode next() {
        return this.next;
    }

    public int getData() {
        return this.data;
    }


    /*
    删除下一个节点
     */
    public boolean removeNext() {
        if (null != next) {
            synchronized (LoopNode.class) {
                if (null != next) {
                    this.next = this.next.next;
                    return true;
                }
            }
        }
        return false;
    }
}

class DoubleLoopNode {
    private int data;
    private DoubleLoopNode pre = this;
    private DoubleLoopNode next = this;

    public DoubleLoopNode(int data) {
        this.data = data;
    }


    public void insert(DoubleLoopNode node) {
        //原节点的下一个节点
        DoubleLoopNode currentNext = this.next;
        //把新节点作为当前节点的下一个节点
        this.next = node;
        //把当前节点作为新节点的前一个节点
        node.pre = this;
        //把原来的下一个节点作为新节点的下一个节点
        node.next = currentNext;
        //把原来的下一个节点的上一个节点作为新节点
        currentNext.pre = node;
    }

    public DoubleLoopNode next() {
        return this.next;
    }

    public DoubleLoopNode pre() {
        return this.pre;
    }

    public int getData() {
        return this.data;
    }


    /*
    删除下一个节点
     */
    public boolean removeNext() {
        if (null != next) {
            synchronized (DoubleLoopNode.class) {
                if (null != next) {
                    this.next = this.next.next;
                    return true;
                }
            }
        }
        return false;
    }
}

public class MyLinkedList {
    public static void main(String[] args) {
        DoubleLoopNode node1 = new DoubleLoopNode(1);
        DoubleLoopNode node2 = new DoubleLoopNode(2);
        DoubleLoopNode node3 = new DoubleLoopNode(3);
        DoubleLoopNode node4 = new DoubleLoopNode(4);
        node1.insert(node2);
        node2.insert(node3);
//        node2.insert(node3);
//        node3.insert(node4);
        System.out.println(node1.pre().getData());
        System.out.println(node1.getData());
        System.out.println(node1.next().getData());
        System.out.println(node2.pre().getData());
        System.out.println(node2.getData());
        System.out.println(node2.next().getData());
//        System.out.println(node3.next().getData());
//        System.out.println(node4.next().getData());

    }
}
