package com.abc.basic.algoritms.algs4.weightedgraph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.tree.UF;
import com.abc.basic.algoritms.algs4.tree.MinPQ;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

public class KruskalMST {
    private static final double FLOATING_POINT_EPSILON = 1E-12;

    private double weight;  // weight of MST
    //最小生成树
    private Queue<Edge> mstTree = new Queue<Edge>();// edges in MST

    public KruskalMST(EdgeWeightedGraph G) {
        // more efficient to build heap by passing array of edges
        //边存放在最小堆中
        //插入时已经排好序
        MinPQ<Edge> pqCut = new MinPQ<Edge>();
        for (Edge e : G.edges()) {
            pqCut.insert(e);
        }

        //贪婪算法
        UF uf = new UF(G.V());
        while (!pqCut.isEmpty() && mstTree.size() < G.V() - 1) {
            //从pq获得权重最小的边和它的顶点
            //边最小堆中删除
            Edge e = pqCut.delMin();
            int v = e.either();
            int w = e.other(v);
            // v-w does not create a cycle
            if (!uf.connected(v, w)) {
                // merge v and w components
                //合并分量
                uf.union(v, w);
                // add edge e to mst
                //得到添加到最小生成树树中
                //合并之后加入队列中
                mstTree.enqueue(e);
                weight += e.weight();
            }
        }
        // check optimality conditions
        assert check(G);
    }

    public Iterable<Edge> edges() {
        return mstTree;
    }

    public double weight() {
        return weight;
    }

    private boolean check(EdgeWeightedGraph G) {

        // check total weight
        double total = 0.0;
        for (Edge e : edges()) {
            total += e.weight();
        }
        if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): " +
                    "%f vs. %f\n", total, weight());
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
        EdgeWeightedGraph G = EdgeWeightedGraph.buildEdgeWeightedGraph();

        KruskalMST mst = new KruskalMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n", mst.weight());
    }

}