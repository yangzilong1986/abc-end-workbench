package com.abc.basic.algoritms.algs4.digraph;

import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 寻找有向环
 */
public class DirectedCycle {
    private boolean[] marked;        // marked[v] = has vertex v been marked?
    //有向环中的所有顶点
    private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
    //递归调用的栈上的所有顶点
    private boolean[] onStack;       // onStack[v] = is vertex on the stack?
    private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)

    /**
     * Determines whether the digraph {@code G} has a directed cycle and, if so,
     * finds such a cycle.
     * @param G the digraph
     */
    public DirectedCycle(Digraph G) {
        marked  = new boolean[G.V()];
        onStack = new boolean[G.V()];
        edgeTo  = new int[G.V()];
        for (int v = 0; v < G.V(); v++)
            if (!marked[v] && cycle == null)
                dfs(G, v);
    }

    // check that algorithm computes either the topological order or finds a directed cycle
    private void dfs(Digraph G, int v) {
        onStack[v] = true;
        marked[v] = true;
        for (int w : G.adj(v)) {

            // short circuit if directed cycle found
            if (cycle != null) {
                return;
            }else if (!marked[w]) {
                edgeTo[w] = v;
                dfs(G, w);
            }else if (onStack[w]) {
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x]) {
                    cycle.push(x);
                }
                cycle.push(w);
                cycle.push(v);
                assert check();
            }
        }//for-over
        onStack[v] = false;
    }

    /**
     * Does the digraph have a directed cycle?
     * @return {@code true} if the digraph has a directed cycle, {@code false} otherwise
     */
    public boolean hasCycle() {
        return cycle != null;
    }

    public Iterable<Integer> cycle() {
        return cycle;
    }

    private boolean check() {

        if (hasCycle()) {
            // verify cycle
            int first = -1, last = -1;
            for (int v : cycle()) {
                if (first == -1) first = v;
                last = v;
            }
            if (first != last) {
                System.err.printf("cycle begins with %d and ends with %d\n", first, last);
                return false;
            }
        }


        return true;
    }

    /**
     * Unit tests the {@code DirectedCycle} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Digraph G =Digraph.buildDigraph();
        DirectedCycle finder = new DirectedCycle(G);
        if (finder.hasCycle()) {
            StdOut.print("Directed cycle: ");
            for (int v : finder.cycle()) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
    }

}