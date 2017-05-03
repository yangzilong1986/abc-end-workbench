package edu.princeton.cs.algs4.digraph;

import edu.princeton.cs.algs4.col.Stack;
import edu.princeton.cs.algs4.utils.StdOut;
import edu.princeton.cs.algs4.utils.In;

import java.util.Iterator;

public class NonrecursiveDirectedDFS {
    private boolean[] marked;  // marked[v] = is there an s->v path?
    /**
     * Computes the vertices reachable from the source vertex {@code s} in the digraph {@code G}.
     * @param  G the digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public NonrecursiveDirectedDFS(Digraph G, int s) {
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

    /**
     * Is vertex {@code v} reachable from the source vertex {@code s}?
     * @param  v the vertex
     * @return {@code true} if vertex {@code v} is reachable from the source vertex {@code s},
     *         and {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
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

    /**
     * Unit tests the {@code NonrecursiveDirectedDFS} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
//        Digraph G = new Digraph(in);
        Digraph G =null;// new Digraph(in);
        int s = Integer.parseInt(args[1]);
        NonrecursiveDirectedDFS dfs = new NonrecursiveDirectedDFS(G, s);
        for (int v = 0; v < G.V(); v++)
            if (dfs.marked(v))
                StdOut.print(v + " ");
        StdOut.println();
    }

}