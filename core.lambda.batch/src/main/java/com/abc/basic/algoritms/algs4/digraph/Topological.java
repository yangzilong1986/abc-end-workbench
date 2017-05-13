package com.abc.basic.algoritms.algs4.digraph;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 拓扑排序
 */
public class Topological {
    private Iterable<Integer> order;  // topological order
    private int[] rank;               // rank[v] = position of vertex v in topological order

    public Topological(Digraph G) {
        DirectedCycle finder = new DirectedCycle(G);
        if (!finder.hasCycle()) {//无环
            //深度优先遍历
            DepthFirstOrder dfs = new DepthFirstOrder(G);
            order = dfs.reversePost();
            rank = new int[G.V()];
            int i = 0;
            for (int v : order) {
                rank[v] = i++;
            }
        }
    }

    public Topological(EdgeWeightedDigraph G) {
        EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(G);
        if (!finder.hasCycle()) {
            DepthFirstOrder dfs = new DepthFirstOrder(G);
            order = dfs.reversePost();
        }
    }

    public Iterable<Integer> order() {
        return order;
    }

    public boolean hasOrder() {
        return order != null;
    }

    @Deprecated
    public boolean isDAG() {
        return hasOrder();
    }

    public int rank(int v) {
        validateVertex(v);
        if (hasOrder()) {
            return rank[v];
        } else {
            return -1;
        }
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = rank.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    public static void main(String[] args) {
        String delimiter = "/";
        SymbolDigraph sg = new SymbolDigraph(In.PATH_NAME+"jobs.txt", delimiter);
        Topological topological = new Topological(sg.digraph());
        for (int v : topological.order()) {
            StdOut.println(sg.nameOf(v));
        }
    }

}
