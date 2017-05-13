package com.abc.basic.algoritms.algs4.digraph;


import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.tree.IndexMinPQ;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  The {@code DijkstraSP} class represents a data type for solving the
 *  single-source shortest paths problem in edge-weighted digraphs
 *  where the edge weights are nonnegative.
 *  <p>
 *  This implementation uses Dijkstra's algorithm with a binary heap.
 *  基于堆的实现
 *
 *  Dijkstra权重有向图最短路径算法，非负值
 *  类似于Prim方法
 */
public class DijkstraSP {
    //从s到v已知的最短距离
    //不可到达的顶点的距离为POSITIVE_INFINITY
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    //最短路径树中的边
    //edgeTo[w] = e;//边e为w为终点v->顶点w
    private DirectedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path

    private IndexMinPQ<Double> pqCut;    // priority queue of vertices

    /**
     * 有向权重图
     * @param G
     * @param s
     */
    public DijkstraSP(EdgeWeightedDigraph G, int s) {
        for (DirectedEdge e : G.edges()) {
            if (e.weight() < 0) {
                throw new IllegalArgumentException("edge " + e + " has negative weight");
            }
        }

        distTo = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];

        validateVertex(s);

        for (int v = 0; v < G.V(); v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pqCut = new IndexMinPQ<Double>(G.V());
        pqCut.insert(s, distTo[s]);
        while (!pqCut.isEmpty()) {
            int v = pqCut.delMin();//队列中的顶点
            for (DirectedEdge e : G.adj(v)) {//访问每个边
                relax(e);
            }
        }
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();//边
            //边w为终端，e为起点，以w为起点
            edgeTo[w] = e;//边e为w为终点v->w顶点

            if (pqCut.contains(w)) {//需要降低优先级，是它先被发现
                pqCut.decreaseKey(w, distTo[w]);
            }else{
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

    public Iterable<DirectedEdge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }

    private boolean check(EdgeWeightedDigraph G, int s) {

        // check that edge weights are nonnegative
        for (DirectedEdge e : G.edges()) {
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
            if (v == s) {
                continue;
            }
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                int w = e.to();
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) {
                continue;
            }
            DirectedEdge e = edgeTo[w];
            int v = e.from();
            if (w != e.to()) {
                return false;
            }
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
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v
                    + " is not between 0 and " + (V - 1));
        }
    }

    public static void main(String[] args) {
//        % java DijkstraSP tinyEWD.txt 0
        In in = new In(In.PATH_NAME+"tinyEWD.txt");
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
        int s =0;

        // compute shortest paths
        DijkstraSP sp = new DijkstraSP(G, s);
        // print shortest path
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
                StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
                for (DirectedEdge e : sp.pathTo(t)) {
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