package edu.princeton.cs.algs4.graph;

import edu.princeton.cs.algs4.col.Stack;
import edu.princeton.cs.algs4.utils.StdOut;
import edu.princeton.cs.algs4.utils.In;

import java.util.Iterator;

/**
 *  The {@code NonrecursiveDFS} class represents a data type for finding
 *  the vertices connected to a source vertex <em>s</em> in the undirected
 *  graph.
 *  <p>
 *  This implementation uses a nonrecursive version of depth-first search
 *  with an explicit stack.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space (not including the graph) proportional to <em>V</em>.
 */
public class NonrecursiveDFS {
    private boolean[] marked;  // marked[v] = is there an s-v path?
    /**
     * Computes the vertices connected to the source vertex {@code s} in the graph {@code G}.
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public NonrecursiveDFS(Graph G, int s) {
        marked = new boolean[G.V()];

        validateVertex(s);

        // to be able to iterate over each adjacency list, keeping track of which
        // vertex in each adjacency list needs to be explored next
        Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
        for (int v = 0; v < G.V(); v++)
            adj[v] = G.adj(v).iterator();

        // depth-first search using an explicit stack
        Stack<Integer> stack = new Stack<Integer>();
        marked[s] = true;
        stack.push(s);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            if (adj[v].hasNext()) {
                int w = adj[v].next();
                // StdOut.printf("check %d\n", w);
                if (!marked[w]) {
                    // discovered vertex w for the first time
                    marked[w] = true;
                    // edgeTo[w] = v;
                    stack.push(w);
                    // StdOut.printf("dfs(%d)\n", w);
                }
            }
            else {
                // StdOut.printf("%d done\n", v);
                stack.pop();
            }
        }
    }

    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Graph G =Graph.buildGraph();
        int s = Integer.parseInt(args[1]);
        NonrecursiveDFS dfs = new NonrecursiveDFS(G, s);
        for (int v = 0; v < G.V(); v++)
            if (dfs.marked(v))
                StdOut.print(v + " ");
        StdOut.println();
    }


}