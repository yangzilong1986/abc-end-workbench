package com.abc.basic.algoritms.algs4.graph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import com.abc.basic.algoritms.algs4.weightedgraph.Edge;
import com.abc.basic.algoritms.algs4.weightedgraph.EdgeWeightedGraph;

/**
 * 图的联通性
 * 深度优先搜索的一个应用就是找出一幅图的所有连通分量
 */
public class CC {
    private boolean[] marked;   // marked[v] = has vertex v been marked?
    private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[id] = number of vertices in given component
    private int count;          // number of connected components

    public CC(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
                count++;
            }
        }
    }

    // depth-first search for a Graph
    private void dfs(Graph G, int v) {
        marked[v] = true;
        id[v] = count;
        size[count]++;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    /**
     * 带权重图
     * @param G
     */
    public CC(EdgeWeightedGraph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {//每个顶点执行一遍，当没有遍历过的进行一次深度遍历
                dfs(G, v);
                count++;//连通计数，分类方式
            }
        }
    }



    // depth-first search for an EdgeWeightedGraph
    private void dfs(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        id[v] = count;//分类方式
        size[count]++;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    /**
     * v 所在的连通分量的标识符（0 ～ count()-1）
     * @param v
     * @return
     */
    public int id(int v) {
        validateVertex(v);
        return id[v];
    }

    public int size(int v) {
        validateVertex(v);
        return size[id[v]];
    }

    /**
     * 连通分量数
     * @return
     */
    public int count() {
        return count;
    }

    /**
     * v 和w 连通吗
     * @param v
     * @param w
     * @return
     */
    public boolean connected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id(v) == id(w);
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    public static void main(String[] args) {
//        Graph G =Graph.buildGraphCC();
        Graph G =new Graph(new In(In.PATH_NAME+"tinyG.txt"));
        CC cc = new CC(G);

        // number of connected components
        int m = cc.count();
        StdOut.println(m + " components");

        // compute list of vertices in each connected component
        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < G.V(); v++) {
            components[cc.id(v)].enqueue(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : components[i]) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
    }
}
