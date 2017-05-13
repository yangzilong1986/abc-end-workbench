package com.abc.basic.algoritms.algs4.digraph;


import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.weightedgraph.EdgeWeightedGraph;
import com.abc.basic.algoritms.algs4.tree.IndexMinPQ;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import com.abc.basic.algoritms.algs4.weightedgraph.Edge;

/**
 *  The {@code DijkstraUndirectedSP} class represents a data type for solving
 *  the single-source shortest paths problem in edge-weighted graphs
 *  where the edge weights are nonnegative.
 *  <p>
 *  This implementation uses Dijkstra's algorithm with a binary heap.
 */
public class DijkstraUndirectedSP {
    private double[] distTo; // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;// edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pqCut;    // priority queue of vertices

    public DijkstraUndirectedSP(EdgeWeightedGraph G, int s) {
        for (Edge e : G.edges()) {
            if (e.weight() < 0) {
                throw new IllegalArgumentException("edge " + e + " has negative weight");
            }
        }

        distTo = new double[G.V()];
        edgeTo = new Edge[G.V()];

        validateVertex(s);

        for (int v = 0; v < G.V(); v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pqCut = new IndexMinPQ<Double>(G.V());
        pqCut.insert(s, distTo[s]);
        while (!pqCut.isEmpty()) {
            int v = pqCut.delMin();
            for (Edge e : G.adj(v))
                relax(e, v);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pqCut.contains(w)) {
                pqCut.decreaseKey(w, distTo[w]);
            }
            else                {
                pqCut.insert(w, distTo[w]);
            }
        }
    }

    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }

    private boolean check(EdgeWeightedGraph G, int s) {

        // check that edge weights are nonnegative
        for (Edge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                int w = e.other(v);
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            if (w != e.either() && w != e.other(e.either())) return false;
            int v = e.other(w);
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
//        % java DijkstraUndirectedSP tinyEWG.txt 6
        In in = new In(In.PATH_NAME+"tinyEWG.txt");
        EdgeWeightedGraph G =new EdgeWeightedGraph(in);
        int s = 6;

        // compute shortest paths
        DijkstraUndirectedSP sp = new DijkstraUndirectedSP(G, s);

        // print shortest path
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
                StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
                for (Edge e : sp.pathTo(t)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, t);
            }
        }
    }

}