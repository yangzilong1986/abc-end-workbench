package com.abc.basic.algoritms.algs4.weightedgraph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.tree.MinPQ;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 * 最小生成树的递归算法
 * Prim算法是逐渐增加树边的算法，它需要一个横切边（包括失效的边）
 */
public class RecursionPrimMST {
    //总权重
    private double weight;       // total weight of MST
    //最小生成树的顶点
    private boolean[] marked;
    //最小生成树的边
    private Queue<Edge> mstTree;
    //横切边
    private MinPQ<Edge> pqCut;

    public RecursionPrimMST(EdgeWeightedGraph G){
        mstTree = new Queue<Edge>();
        pqCut = new MinPQ<Edge>();
        marked = new boolean[G.V()];
        // run Prim from all vertices to
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                prim(G, v);     // get a minimum spanning forest
            }
        }

    }
    private void prim(EdgeWeightedGraph G, int s) {
        scan(G, 0);//假设G是联通的
        while(!pqCut.isEmpty()){
            Edge e=pqCut.delMin();
            int v=e.either();
            int w=e.other(v);
            //跳过失败边
            if(marked[v]&&marked[w]){
                continue;
            }
            mstTree.enqueue(e);
            weight += e.weight();
            if(!marked[v]){
                scan(G,v);
            }
            if(!marked[w]){
                scan(G,w);
            }
        }
    }
    public Iterable<Edge> edges() {
        return mstTree;
    }

    public double weight() {
        return weight;
    }

    private void scan(EdgeWeightedGraph G, int v) {
        assert !marked[v];
        //标记顶点V并将所有连接v和未被标记顶点的边加入到pqCut中
        marked[v] = true;
        for (Edge e : G.adj(v)){
            if (!marked[e.other(v)]){
                //横切边，包括失效的边的MinPQ
                pqCut.insert(e);
            }
        }

    }

    public static void main(String[] args) {
        EdgeWeightedGraph G = EdgeWeightedGraph.buildEdgeWeightedGraph();
        RecursionPrimMST mst = new RecursionPrimMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n", mst.weight());
    }
}
