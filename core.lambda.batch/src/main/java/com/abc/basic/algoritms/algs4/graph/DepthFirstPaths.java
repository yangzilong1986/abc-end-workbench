package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 */
public class DepthFirstPaths {
    //这个顶点上调用过dfs()了吗？
    private boolean[] marked;    // marked[v] = is there an s-v path?
    // 从起点是根接的的已知路径上的最后一个顶点
    private int[] edgeTo;        // edgeTo[v] = last edge on s-v path
    // 起点
    private final int s;         // source vertex


    public DepthFirstPaths(Graph G, int s) {
        this.s = s;
        edgeTo = new int[G.V()];
        marked = new boolean[G.V()];
        validateVertex(s);
        dfs(G, s);
    }

    // breadth-first search from a single source
    private void dfs2(Graph G, int s) {
        Deque<Integer> stack = new ArrayDeque<Integer>();
        marked[s] = true;// 标记起点
        stack.addLast(s);
        while (!stack.isEmpty()) {
            // 从队列中删去下一顶点
            int v = stack.removeLast();
            for (int w : G.adj(v)) {//属于边，连接表为Bag实现了Iterable接口

            }//
        }
    }

    // depth first search from v
    private void dfs(Graph G, int v) {
        //LIFO，后进先出，即栈
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                edgeTo[w] = v;
                dfs(G, w);
            }
        }
    }


    public boolean hasPathTo(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * 返回点遍历集合
     * @param v
     * @return
     */
    public Iterable<Integer> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) {
            return null;
        }
        Stack<Integer> path = new Stack<Integer>();
        //x初始化为终点
        //从edgeTo中获取它到达的边的另一个端点
        //
        for (int x = v; x != s; x = edgeTo[x]) {
            //进桟
            path.push(x);
        }
        path.push(s);
        return path;
    }

    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " +
                    v + " is not between 0 and " + (V - 1));
        }
    }

    public static void searchPath( Graph G ,int s){
        DepthFirstPaths dfs = new DepthFirstPaths(G, s);
        for (int v = 0; v < G.V(); v++) {
            if (dfs.hasPathTo(v)) {
                StdOut.printf("%d to %d:  ", s, v);
                for (int x : dfs.pathTo(v)) {
                    if (x == s)
                        StdOut.print(x);
                    else
                        StdOut.print("-" + x);
                }
                StdOut.println();
            }
        }
    }
    /**
     * Unit tests the {@code DepthFirstPaths} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Graph G = Graph.buildGraph();
        int s =0;
        DepthFirstPaths dfs = new DepthFirstPaths(G, s);
        int vv=5;
        for (int x : dfs.pathTo(vv)) {
            if (x == s)
                StdOut.print(x);
            else
                StdOut.print("-" + x);
        }
        //0-2-3-5
    }

}