/******************************************************************************
 *  Compilation:  javac Cycle.java
 *  Execution:    java  Cycle filename.txt
 *  Dependencies: Graph.java Stack.java In.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/41graph/tinyG.txt
 *                http://algs4.cs.princeton.edu/41graph/mediumG.txt
 *                http://algs4.cs.princeton.edu/41graph/largeG.txt  
 *
 *  Identifies a cycle.
 *  Runs in O(E + V) time.
 *
 *  % java Cycle tinyG.txt
 *  3 4 5 3 
 * 
 *  % java Cycle mediumG.txt 
 *  15 0 225 15 
 * 
 *  % java Cycle largeG.txt 
 *  996673 762 840164 4619 785187 194717 996673 
 *
 ******************************************************************************/

package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  This implementation uses depth-first search.
 *  检查环
 */
public class Cycle {
    private boolean[] marked;
    private int[] edgeTo;
    private Stack<Integer> cycle;

    public Cycle(Graph G) {
        if (hasSelfLoop(G)) {
            return;
        }
        if (hasParallelEdges(G)) {
            return;
        }
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
//        dfs(G, -1, 0);
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, -1, v);
            }
        }
    }


    // does this graph have a self loop?
    // side effect: initialize cycle to be self loop
    private boolean hasSelfLoop(Graph G) {
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                if (v == w) {
                    cycle = new Stack<Integer>();
                    cycle.push(v);
                    cycle.push(v);
                    return true;
                }
            }
        }
        return false;
    }

    // does this graph have two parallel edges?
    // side effect: initialize cycle to be two parallel edges
    private boolean hasParallelEdges(Graph G) {
        marked = new boolean[G.V()];

        for (int v = 0; v < G.V(); v++) {
            // check for parallel edges incident to v
            for (int w : G.adj(v)) {
                if (marked[w]) {
                    cycle = new Stack<Integer>();
                    cycle.push(v);
                    cycle.push(w);
                    cycle.push(v);
                    return true;
                }
                marked[w] = true;
            }

            // reset so marked[v] = false for all v
            for (int w : G.adj(v)) {
                marked[w] = false;
            }
        }
        return false;
    }


    public boolean hasCycle() {
        return cycle != null;
    }

    public Iterable<Integer> cycle() {
        return cycle;
    }

    private void dfs(Graph G, int u, int v) {//u输入为-1，递归时：u:0,v:6
        marked[v] = true;
        for (int w : G.adj(v)) {//遍历一个边的另一个端点，0连接的另一个端点1,2,5,6

            // short circuit if cycle already found
            if (cycle != null) {
                return;
            }
            if (!marked[w]) {//false时表示没有遍历过
                edgeTo[w] = v;//端点连接v为前一个顶点，w为后一个顶点
                dfs(G, v, w);//访问,递归时，中断进入再次进入方法，v为前一个顶点，w为后一个顶点，一个边的两个顶点
                //递归之后返回继续执行
            }else if (w != u) {//a-b-c: u递归方法传入的参数为a，v是b。w是本次访问的顶点b连接的顶点为c。
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x]) {
                    cycle.push(x);
                }
                cycle.push(w);
                cycle.push(v);
            }else{

            }

        }
    }

    public static void main(String[] args) {
//        Graph G =Graph.buildCC();
        Graph G =new Graph(new In(In.PATH_NAME+"tinyG.txt"));
        Cycle finder = new Cycle(G);
        if (finder.hasCycle()) {
            for (int v : finder.cycle()) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
        else {
            StdOut.println("Graph is acyclic");
        }
    }


}