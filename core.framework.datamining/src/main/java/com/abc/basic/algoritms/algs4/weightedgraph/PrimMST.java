package com.abc.basic.algoritms.algs4.weightedgraph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import com.abc.basic.algoritms.algs4.tree.UF;
import com.abc.basic.algoritms.algs4.tree.IndexMinPQ;
import com.abc.basic.algoritms.algs4.utils.In;

/**
 * Prim最小生成树，每一步为树添加一条边
 */
public class PrimMST {
    private static final double FLOATING_POINT_EPSILON = 1E-12;
    //如果顶点v不在树中但是至少包含一条边和树相连，那么edgeTo是将v和树连接的最短边
    //distTo是这边边的权重

    // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    //edgeTo[v]中的v是将v连接到树中的对象
    //edgeTo[w] = e;//w连接到的另一个顶点e
    private Edge[] edgeTo;//距离最近的边

    //distTo[w] = e.weight();
    //distTo[v] = weight of shortest such edge
    private double[] distTo;//权重

    // marked[v] = true if v on tree, false otherwise
    //节点是否访问过，即在树中的节点时，则为true，否则为false
    private boolean[] marked;

    //索引优先队列，横切边
    //所有这类的顶点都保存在一条索引优先队列中，索引v关联的值是edgeTo[v]的边的权重
    //队列保存最小生成树中边
    private IndexMinPQ<Double> pqCut;

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public PrimMST(EdgeWeightedGraph G) {
        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        pqCut = new IndexMinPQ<Double>(G.V());
        for (int v = 0; v < G.V(); v++){
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        // run from each vertex to find
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {//没有访问的顶点
                prim(G, v); // minimum spanning forest
            }
        }
        // check optimality conditions
        assert check(G);
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(EdgeWeightedGraph G, int s) {
        distTo[s] = 0.0;
        pqCut.insert(s, distTo[s]);
        while (!pqCut.isEmpty()) {
            int v = pqCut.delMin();//获取最小的
            scan(G, v);
        }
    }

    // scan vertex v
    private void scan(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);//边的另一个顶点
            if (marked[w]) {
                // v-w is obsolete edge
                //已经遍历过了
                continue;
            }
            if (e.weight() < distTo[w]) {
                //连接w和树的最佳边Edge变为e
                distTo[w] = e.weight();
                edgeTo[w] = e;//w连接到的另一个顶点e
                if (pqCut.contains(w)) {//包含则，更新
                    pqCut.decreaseKey(w, distTo[w]);
                }else {
                    pqCut.insert(w, distTo[w]);
                }
            }
        }
    }

    public Iterable<Edge> edges() {
        Queue<Edge> mst = new Queue<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.enqueue(e);
            }
        }
        return mst;
    }

    public double weight() {
        double weight = 0.0;
        for (Edge e : edges()) {
            weight += e.weight();
        }
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
            System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n",
                    totalWeight, weight());
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
            for (Edge f : edges()) {
                int x = f.either(), y = f.other(x);
                if (f != e) uf.union(x, y);
            }

            // check that e is min weight edge in crossing cut
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (!uf.connected(x, y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f + " violates cut optimality conditions");
                        return false;
                    }
                }
            }

        }

        return true;
    }

    public static void main(String[] args) {
        //java PrimMST tinyEWG.txt
        In in = new In(In.PATH_NAME + "tinyEWG.txt");
//        EdgeWeightedGraph G = EdgeWeightedGraph.buildEdgeWeightedGraph();
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        PrimMST mst = new PrimMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n", mst.weight());
    }


}