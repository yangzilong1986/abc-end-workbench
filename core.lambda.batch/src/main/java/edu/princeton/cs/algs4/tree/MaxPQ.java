package edu.princeton.cs.algs4.tree;

import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 *  基于堆的优先队列
 *  位置k的父节点位置为k/2
 *  位于k的左子节点2k 2k+1
 *
 * 删除最大元素和插入元素，性质形成的队列为优先队列
 *
 * 优先队列基于二叉堆结构的经典实现
 */

public class MaxPQ<Key> implements Iterable<Key> {
    //基于堆的完全二叉树
    private Key[] pq; // store items at indices 1 to n
    private int n;   // number of items on priority queue
    private Comparator<Key> comparator;  // optional Comparator

    public MaxPQ(int initCapacity) {
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    public MaxPQ() {
        this(1);
    }

    public MaxPQ(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    public MaxPQ(Comparator<Key> comparator) {
        this(1, comparator);
    }

    public MaxPQ(Key[] keys) {
        n = keys.length;
        pq = (Key[]) new Object[keys.length + 1]; 
        for (int i = 0; i < n; i++) {
            pq[i + 1] = keys[i];
        }
        //堆的下沉
        for (int k = n/2; k >= 1; k--) {
            sink(k);
        }
        assert isMaxHeap();
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public Key max() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Key[] temp = (Key[]) new Object[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    /**
     * 插入元素，使用swim
     * @param x
     */
    public void insert(Key x) {

        // double size of array if necessary
        if (n >= pq.length - 1) {
            resize(2 * pq.length);
        }
        // add x, and percolate it up to maintain heap invariant
        //插入的元素放入到数组额最后
        pq[++n] = x;
        swim(n);//最后一个元素迁移
        assert isMaxHeap();
    }

    public Key delMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        Key max = pq[1];//从根节点获取最大元素
        exch(1, n--);//把删除元素和最后一个元素换位置
        //回复堆的有序性
        sink(1);
        //换位置后，原来数组中最后一个元素已经游离在堆部分外
        pq[n+1] = null; // to avoid loiterig and help with garbage collection
        if ((n > 0) && (n == (pq.length - 1) / 4)) {
            resize(pq.length / 2);
        }
        assert isMaxHeap();
        return max;
    }

    /**
     * 上浮
     * @param k
     */
    private void swim(int k) {
        //k/2为其父元素
        while (k > 1 && less(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    /**
     * 由上到下的堆有序化，下沉
     * @param k
     */
    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(j, j+1)) {//k的两个孩子节点。j小于j+1
                j++;
            }
            if (!less(k, j)) {//父元素不小于两个子元素
                break;
            }
            //子节点大于父节点，则交互这两个节点
            exch(k, j);
            //移到下一个子节点，作为父节点
            k = j;
        }
    }

    private boolean less(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) pq[i]).compareTo(pq[j]) < 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) < 0;
        }
    }

    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    // is pq[1..N] a max heap?
    private boolean isMaxHeap() {
        return isMaxHeap(1);
    }

    // is subtree of pq[1..n] rooted at k a max heap?
    private boolean isMaxHeap(int k) {
        if (k > n)
            return true;
        int left = 2*k;
        int right = 2*k + 1;
        if (left  <= n && less(k, left)) {
            return false;
        }
        if (right <= n && less(k, right)) {
            return false;
        }
        return isMaxHeap(left) && isMaxHeap(right);
    }

    public Iterator<Key> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Key> {

        // create a new pq
        private MaxPQ<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null)
                copy = new MaxPQ<Key>(size());
            else
                copy = new MaxPQ<Key>(size(), comparator);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext()  {
            return !copy.isEmpty();
        }

        public void remove()      {
            throw new UnsupportedOperationException();
        }

        public Key next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return copy.delMax();
        }
    }

    public static void main(String[] args) {
        MaxPQ<String> pq = new MaxPQ<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-")) pq.insert(item);
            else if (!pq.isEmpty()) StdOut.print(pq.delMax() + " ");
        }
        StdOut.println("(" + pq.size() + " left on pq)");
    }

}