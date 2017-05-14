package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    //创建一个最大容量为maxN的优先队列，索引范围为0~maxN-1
    private int maxN;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ

    //基于索引的二叉堆。它用于保存索引
    //heapBasedIndexing中的索引也就是Key的索引,起始index为1
    //索引二叉堆，索引由1开始
    // binary heap using 1-based indexing
    //数据索引的真实位置
    //heapBasedIndexing[1]表示第一个元素的索引
    //比较时比较Key值，变化时不改变keys的数组，
    // 而是改变heapBasedIndexing数组的内容
    private int[] heapBasedIndexing;
    //元素的值keys[heapBasedIndexing[1]];
    private Key[] keys;      // keys[i] = priority of i

    //保存heapBasedIndexing的逆序，
    // qp[i]的i值在heapBasedIndexing中的位置，即索引j，heapBasedIndexing[j]=i
    //它的值和keys的值一致
    //inverse of heapBasedIndexing -
    //indexedForIndexKey[heapBasedIndexing[i]] = heapBasedIndexing[indexedForIndexKey[i]] = i
    //用于修改队列中元素的api
    /**
     * public void decreaseKey(int i, Key key) {
     * keys[i] = key;
     * swim(indexedForIndexKey[i]);
     * }
     **/
    private int[] indexedForIndexKey;

    /**
     * 算法描述
     * String[] strings = { "it", "was", "the", "best", "of" };
     * heapBasedIndexing = {IndexMinPQ@467}
     * maxN = 5
     * n = 5
     * heapBasedIndexing   indexedForIndexKey
     * 0 = 0                 0 = 2
     * 1 = 3                 1 = 4
     * 2 = 0                 2 = 3
     * 3 = 2                 3 = 1
     * 4 = 1                 4 = 5
     * 5 = 4                 5 = -1
     * keys = {Comparable[6]@474}
     * 0 = "it"                 取值：keys[heapBasedIndexing[1]]
     * 1 = "was"                heapBasedIndexing[1];//3 即;
     * 2 = "the"
     * 3 = "best"                keys[3]
     * 4 = "of"
     */
    public IndexMinPQ(int maxN) {
        if (maxN < 0) throw new IllegalArgumentException();
        this.maxN = maxN;
        n = 0;
        keys = (Key[]) new Comparable[maxN + 1]; // make this of length maxN??

        indexedForIndexKey = new int[maxN + 1]; // make this of length maxN??

        heapBasedIndexing = new int[maxN + 1];
        for (int i = 0; i <= maxN; i++) {
            indexedForIndexKey[i] = -1;
        }
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public boolean contains(int i) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        return indexedForIndexKey[i] != -1;
    }

    public int size() {
        return n;
    }

    public int delMin() {
        if (n == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }

        int min = heapBasedIndexing[1];//3
        exch(1, n--);//把第一个元素和最后一个交换位置，交换之后修改元素大小n
        sink(1);//删除之后的元素再排序
        assert min == heapBasedIndexing[n + 1];

        //值的位置，堆操作把1处的值删除
        indexedForIndexKey[min] = -1; //初始化时为-1 delete
        keys[min] = null; // to help with garbage collection

        heapBasedIndexing[n + 1] = -1; // not needed
        return min;
    }

    /**
     * 插入元素，并把位置与key关联
     *
     * @param i
     * @param key
     */
    public void insert(int i, Key key) {//0 A 1 B
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (contains(i)) {
            throw new IllegalArgumentException("index is already in the priority queue");
        }
        n++;

        //元素key的位置
        keys[i] = key;

        //索引和值的位置
        //inverseQP为元素索引，同key的索引一致,它保存当前元素索引即n
        //i为数据的位置
        indexedForIndexKey[i] = n;//索引
        //在堆中的位置，它的值为key的索引
        //元素的索引位置n存储i
        //元素的索引在堆中的位置
        heapBasedIndexing[n] = i;


        //n最后一个的元素的位置
        swim(n);
    }

    private void exch(int i, int j) {
        int swap = heapBasedIndexing[i];//heapBasedIndexing中存储key的索引//swap:3
        heapBasedIndexing[i] = heapBasedIndexing[j];
        heapBasedIndexing[j] = swap;
        indexedForIndexKey[heapBasedIndexing[i]] = i;//索引的索引
        indexedForIndexKey[heapBasedIndexing[j]] = j;
    }

    public void decreaseKey(int i, Key key) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        if (keys[i].compareTo(key) <= 0) {
            throw new IllegalArgumentException("Calling decreaseKey() " +
                    "with given argument would not strictly decrease the key");
        }

        keys[i] = key;
        swim(indexedForIndexKey[i]);
    }

    public Key minKey() {
        if (n == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return keys[heapBasedIndexing[1]];
    }

    private boolean greater(int i, int j) {
        //i=1 heapBasedIndexing[i]=0    1 0
        //j=2  heapBasedIndexing[j]=1   3 2
        return keys[heapBasedIndexing[i]].compareTo(keys[heapBasedIndexing[j]]) > 0;
    }

    //上浮
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    //下浮
    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }

    public int minIndex() {
        if (n == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return heapBasedIndexing[1];
    }

    public Key keyOf(int i) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        } else {
            return keys[i];
        }
    }

    public void changeKey(int i, Key key) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        keys[i] = key;
        swim(indexedForIndexKey[i]);
        sink(indexedForIndexKey[i]);
    }

    @Deprecated
    public void change(int i, Key key) {
        changeKey(i, key);
    }


    public void increaseKey(int i, Key key) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        if (keys[i].compareTo(key) >= 0) {
            throw new IllegalArgumentException("Calling increaseKey() " +
                    "with given argument would not strictly increase the key");
        }
        keys[i] = key;
        sink(indexedForIndexKey[i]);
    }

    public void delete(int i) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        int index = indexedForIndexKey[i];
        exch(index, n--);
        swim(index);
        sink(index);
        keys[i] = null;
        indexedForIndexKey[i] = -1;
    }


    public Iterator<Integer> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
        // create a new heapBasedIndexing
        private IndexMinPQ<Key> copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            copy = new IndexMinPQ<Key>(heapBasedIndexing.length - 1);
            for (int i = 1; i <= n; i++)
                copy.insert(heapBasedIndexing[i], keys[heapBasedIndexing[i]]);
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return copy.delMin();
        }
    }

    public static void main(String[] args) {
        // insert a bunch of strings
        String[] strings = {"it", "was", "the", "best", "of"};

        IndexMinPQ<String> heapBasedIndexing = new IndexMinPQ<String>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            heapBasedIndexing.insert(i, strings[i]);
        }

        // delete and print each key
        while (!heapBasedIndexing.isEmpty()) {
            int i = heapBasedIndexing.delMin();
            StdOut.println(i + " " + strings[i]);
        }
        StdOut.println();

        // reinsert the same strings
        for (int i = 0; i < strings.length; i++) {
            heapBasedIndexing.insert(i, strings[i]);
        }

        // print each key using the iterator
        for (int i : heapBasedIndexing) {
            StdOut.println(i + " " + strings[i]);
        }
        while (!heapBasedIndexing.isEmpty()) {
            heapBasedIndexing.delMin();
        }

    }
}