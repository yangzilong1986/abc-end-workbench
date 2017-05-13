package com.abc.basic.algoritms.algs4.weightedgraph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.tree.MinPQ;
import com.abc.basic.algoritms.algs4.tree.UF;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 任意加权联通图的最小生成树
 */
public class LazyPrimMST {
    private static final double FLOATING_POINT_EPSILON = 1E-12;
    //总权重
    private double weight;       // total weight of MST
    //最小生成树的边
    private Queue<Edge> mstTree;     // edges in the MST
    //最小生成树的边
    private boolean[] marked;    // marked[v] = true if v on tree
    //横切边，包括失效的边的MinPQ
    private MinPQ<Edge> pqCut;      // edges with one endpoint in tree

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public LazyPrimMST(EdgeWeightedGraph G) {
        mstTree = new Queue<Edge>();
        pqCut = new MinPQ<Edge>();
        marked = new boolean[G.V()];
        // run Prim from all vertices to
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                prim(G, v);
            }
        }
        // check optimality conditions
        assert check(G);
    }

    // run Prim's algorithm
    private void prim(EdgeWeightedGraph G, int s) {
        scan(G, s);//假设是联通的
        // better to stop when mst has V-1 edges
        //横切边，包括失效的边的MinPQ
        while (!pqCut.isEmpty()) {
            // smallest edge on pq
            //从pq中得到权重最小的边
            Edge e = pqCut.delMin();
            // two endpoints
            int v = e.either(), w = e.other(v);

            assert marked[v] || marked[w];
            // lazy, both v and w already scanned
            //跳过失效的边
            if (marked[v] && marked[w]) {
                continue;
            }
            // add e to MST
            //将边加入到树中
            mstTree.enqueue(e);
            weight += e.weight();
            // v becomes part of tree
            //横切边，包括失效的边的MinPQ
            if (!marked[v]) {
                scan(G, v);
            }
            // w becomes part of tree
            if (!marked[w]) {
                scan(G, w);
            }
        }
    }

    // add all edges e incident to v onto pq if the other endpoint has not yet been scanned
    private void scan(EdgeWeightedGraph G, int v) {
        assert !marked[v];
        //标记顶点V并将所有连接v和未被标记顶点的边加入到pq中
        marked[v] = true;
        for (Edge e : G.adj(v)){
            if (!marked[e.other(v)]){
                //横切边，包括失效的边的MinPQ
                pqCut.insert(e);
            }
        }

    }

    public Iterable<Edge> edges() {
        return mstTree;
    }

    public double weight() {
        return weight;
    }

    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(EdgeWeightedGraph G) {

        // check weight
        double totalWeight = 0.0;
        for (Edge e : edges()) {
            totalWeight += e.weight();
        }
        if (Math.abs(totalWeight - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight():" +
                    " %f vs. %f\n", totalWeight, weight());
            return false;
        }

        // check that it is acyclic
        UF uf = new UF(G.V());
        for (Edge e : edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.connected(v, w)) {
                System.err.println("Not a forest");
                return false;
            }
            uf.union(v, w);
        }

        // check that it is a spanning forest
        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (!uf.connected(v, w)) {
                System.err.println("Not a spanning forest");
                return false;
            }
        }
        // check that it is a minimal spanning forest (cut optimality conditions)
        for (Edge e : edges()) {
            // all edges in MST except e
            uf = new UF(G.V());
            for (Edge f : mstTree) {
                int x = f.either(), y = f.other(x);
                if (f != e) {
                    uf.union(x, y);
                }
            }

            // check that e is min weight edge in crossing cut
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (!uf.connected(x, y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f +
                                " violates cut optimality conditions");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //
        In in = new In(In.PATH_NAME + "tinyEWG.txt");
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        LazyPrimMST mst = new LazyPrimMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n", mst.weight());
    }

}