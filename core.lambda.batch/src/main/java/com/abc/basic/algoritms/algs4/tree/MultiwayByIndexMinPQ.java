/******************************************************************************
 * Compilation:  javac Multiway.java
 * Execution:    java Multiway input1.txt input2.txt input3.txt ...
 * Dependencies: IndexMinPQ.java In.java StdOut.java
 * Data files:   http://algs4.cs.princeton.edu/24pq/m1.txt
 * http://algs4.cs.princeton.edu/24pq/m2.txt
 * http://algs4.cs.princeton.edu/24pq/m3.txt
 * <p>
 * Merges together the sorted input stream given as command-line arguments
 * into a single sorted output stream on standard output.
 * <p>
 * % more m1.txt
 * A B C F G I I Z
 * <p>
 * % more m2.txt
 * B D H P Q Q
 * <p>
 * % more m3.txt
 * A B E F J N
 * <p>
 * % java Multiway m1.txt m2.txt m3.txt
 * A A B B B C D E F F G H I I J N P Q Q Z
 ******************************************************************************/

package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  优先队列的多路并归
 *  将多个有序输入流并归成一个输出
 */

public class MultiwayByIndexMinPQ {

    // This class should not be instantiated.
    private MultiwayByIndexMinPQ() {
    }

    // merge together the sorted input streams and write the sorted result to standard output
    private static void merge(In[] streams) {
        int n = streams.length;
        IndexMinPQ<String> pq = new IndexMinPQ<String>(n);
        //在每个流中读入一个数据，形成index/key
        for (int i = 0; i < n; i++) {
            if (!streams[i].isEmpty()) {
                pq.insert(i, streams[i].readString());
            }
        }
        StdOut.print("读数据 ");
        // Extract and print min and read next from its stream. 
        while (!pq.isEmpty()) {
            //输出
            StdOut.print(pq.minKey() + " ");
            //i为并归流的数量，索引
            int i = pq.delMin();
            if (!streams[i].isEmpty())
                pq.insert(i, streams[i].readString());
        }
        StdOut.println();
    }


    public static void main(String[] args) {
        String PATH_NAME = In.PATH_NAME;
        int n = args.length;
        In[] streams = new In[3];
        streams[0] = new In(PATH_NAME+"m1.txt");
        streams[1] = new In(PATH_NAME+"m2.txt");
        streams[2] = new In(PATH_NAME+"m3.txt");
        merge(streams);
    }
}