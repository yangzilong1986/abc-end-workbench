package edu.princeton.cs.algs4.digraph;

import edu.princeton.cs.algs4.col.Bag;
import edu.princeton.cs.algs4.utils.In;
import edu.princeton.cs.algs4.col.Stack;
import edu.princeton.cs.algs4.utils.StdOut;

import java.util.NoSuchElementException;

/**
 * 有向图
 */
public class Digraph {
    private static final String NEWLINE = System.getProperty("line.separator");
    //顶点数
    private final int V;           // number of vertices in this digraph
    //边数
    private int E;                 // number of edges in this digraph
    //邻接表
    private Bag<Integer>[] adj;    // adj[v] = adjacency list for vertex v
    private int[] indegree;        // indegree[v] = indegree of vertex v

    public Digraph(int V) {
        if (V < 0)
            throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = V;
        this.E = 0;
        indegree = new int[V];
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Integer>();
        }
    }

    public Digraph(Digraph G) {
        this(G.V());
        this.E = G.E();
        for (int v = 0; v < V; v++)
            this.indegree[v] = G.indegree(v);
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<Integer> reverse = new Stack<Integer>();
            for (int w : G.adj[v]) {
                reverse.push(w);
            }
            for (int w : reverse) {
                adj[v].add(w);
            }
        }
    }


    public int V() {
        return V;
    }


    public int E() {
        return E;
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * 添加边
     *
     * @param v
     * @param w
     */
    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        adj[v].add(w);
        indegree[w]++;
        E++;
    }

    /**
     * 由v指出的边，连接的所有顶点
     *
     * @param v
     * @return
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }


    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }


    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }


    public Digraph reverse() {
        Digraph reverse = new Digraph(V);
        for (int v = 0; v < V; v++) {
            for (int w : adj(v)) {
                reverse.addEdge(w, v);
            }
        }
        return reverse;
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(String.format("%d: ", v));
            for (int w : adj[v]) {
                s.append(String.format("%d ", w));
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    public static Digraph buildDigraph() {
        Digraph G = new Digraph(13);
        G.addEdge(4, 2);
        G.addEdge(2, 3);
        G.addEdge(3, 2);
        G.addEdge(6, 0);
        G.addEdge(0, 1);
        G.addEdge(2, 0);
        G.addEdge(11, 12);
        G.addEdge(12, 9);
        G.addEdge(9, 10);
        G.addEdge(9, 11);
        G.addEdge(8, 9);
        G.addEdge(10, 12);
        G.addEdge(11, 4);
        G.addEdge(4, 3);
        G.addEdge(5, 3);
        G.addEdge(7, 8);
        G.addEdge(8, 7);
        G.addEdge(5, 4);
        G.addEdge(0, 5);
        G.addEdge(6, 4);
        G.addEdge(6, 9);
        G.addEdge(7, 6);
        return G;
    }

    public static void main(String[] args) {
        Digraph G =buildDigraph();
        StdOut.println(G);
    }

}