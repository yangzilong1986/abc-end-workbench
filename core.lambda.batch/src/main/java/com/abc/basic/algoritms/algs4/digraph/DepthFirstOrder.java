package com.abc.basic.algoritms.algs4.digraph;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.col.Stack;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

public class DepthFirstOrder {
    private boolean[] marked;          // marked[v] = has v been marked in dfs?

    //有向环中的所有顶点
    private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
    //递归调用的栈上的所有顶点
    private boolean[] onStack;       // onStack[v] = is vertex on the stack?

    private boolean isBipartite;   // is the graph bipartite?
    private boolean[] color;       // color[v] gives vertices on one side of bipartition

    //有向环中的顶点
    private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)


    //所有顶点的前序排列
    private int[] pre;                 // pre[v]    = preorder  number of v
    //所有顶点的后续排列
    private int[] post;                // post[v]   = postorder number of v

    private Queue<Integer> preorder;   // vertices in preorder
    private Queue<Integer> postorder;  // vertices in postorder
    private Stack<Integer> reverse;

    private int preCounter;            // counter for preorder numbering
    private int postCounter;           // counter for postorder numbering

    public DepthFirstOrder(Digraph G) {
        pre    = new int[G.V()];
        post   = new int[G.V()];
        postorder = new Queue<Integer>();
        preorder  = new Queue<Integer>();
        reverse = new Stack<Integer>();
        marked    = new boolean[G.V()];

        onStack = new boolean[G.V()];
        edgeTo  = new int[G.V()];
        color  = new boolean[G.V()];
        isBipartite=true;

        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
            }
        }
        assert check();
    }

    private void dfs(Digraph G, int v) {
        marked[v] = true;
        onStack[v] = true;
        //前序遍历的第几个顶点
        pre[v] = preCounter++;
        preorder.enqueue(v);
        for (int w : G.adj(v)) {//当节点没有另一个边
            if (!marked[w]) {//false时进入
                //没有访问过
                edgeTo[w] = v;

                //连接点的颜色不同
                color[w] = !color[v];
                dfs(G, w);
            }
            else if (onStack[w]) {
                //递归调用栈，在递归栈中,非递归执行代码
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x]) {
                    cycle.push(x);
                }
                cycle.push(w);
                cycle.push(v);
            }

        }//一个边0-5-4，到4之后，一个深度探测到最后，结束
        postorder.enqueue(v);
        reverse.push(v);
        post[v] = postCounter++;
        onStack[v] = false;
    }

    public DepthFirstOrder(EdgeWeightedDigraph G) {
        pre    = new int[G.V()];
        post   = new int[G.V()];
        postorder = new Queue<Integer>();
        preorder  = new Queue<Integer>();
        reverse = new Stack<Integer>();
        marked    = new boolean[G.V()];

        onStack = new boolean[G.V()];
        edgeTo  = new int[G.V()];
        color  = new boolean[G.V()];
        isBipartite=true;
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
            }
        }
    }



    private void dfs(EdgeWeightedDigraph G, int v) {
        marked[v] = true;
        pre[v] = preCounter++;
        preorder.enqueue(v);
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (!marked[w]) {
                dfs(G, w);
            }
        }
        postorder.enqueue(v);
        reverse.push(v);
        post[v] = postCounter++;
    }

    public int pre(int v) {
        validateVertex(v);
        return pre[v];
    }

    public int post(int v) {
        validateVertex(v);
        return post[v];
    }

    public Iterable<Integer> post() {
        return postorder;
    }

    public Iterable<Integer> pre() {
        return preorder;
    }

    public Iterable<Integer> reversePost() {
//        Stack<Integer> reverse = new Stack<Integer>();
//        for (int v : postorder) {
//            reverse.push(v);
//        }
        return reverse;
    }

    public boolean hasPathTo(int v) {
        validateVertex(v);
        return marked[v];
    }

    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * 返回点遍历集合
     * @param v
     * @return
     */
    public Iterable<Integer> pathTo(int s,int v) {
        validateVertex(v);
        if (!hasPathTo(v)) {
            return null;
        }
        Stack<Integer> path = new Stack<Integer>();
        //x初始化为终点
        //从edgeTo中获取它到达的边的另一个端点
        //
        for (int x = v; x != s; x = edgeTo[x]) {
            //进桟
            path.push(x);
        }
        path.push(s);
        return path;
    }

    // check that pre() and post() are consistent with pre(v) and post(v)
    private boolean check() {

        // check that post(v) is consistent with post()
        int r = 0;
        for (int v : post()) {
            if (post(v) != r) {
                StdOut.println("post(v) and post() inconsistent");
                return false;
            }
            r++;
        }
        // check that pre(v) is consistent with pre()
        r = 0;
        for (int v : pre()) {
            if (pre(v) != r) {
                StdOut.println("pre(v) and pre() inconsistent");
                return false;
            }
            r++;
        }

        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }


    public static void main(String[] args) {
        //% java DepthFirstOrder tinyDAG.txt
        Digraph G =new Digraph(new In(In.PATH_NAME+"tinyDAG.txt"));
        DepthFirstOrder dfs = new DepthFirstOrder(G);
//        StdOut.println("   v  pre post");
//        StdOut.println("--------------");
//        for (int v = 0; v < G.V(); v++) {
//            StdOut.printf("%4d %4d %4d\n", v, dfs.pre(v), dfs.post(v));
//        }

//        StdOut.print("12是否可达:  ");
//        int vv=7;
//        int s=8;
//        for (int x : dfs.pathTo(s,vv)) {
//            if (x == s)
//                StdOut.print(x);
//            else
//                StdOut.print("-" + x);
//        }

        StdOut.print("Preorder:  ");
        for (int v : dfs.pre()) {
            StdOut.print(v + " ");
        }
        StdOut.println();

        StdOut.print("Postorder: ");
        for (int v : dfs.post()) {
            StdOut.print(v + " ");
        }
        StdOut.println();

        StdOut.print("Reverse postorder: ");
        for (int v : dfs.reversePost()) {
            StdOut.print(v + " ");
        }
        StdOut.println();


    }

}
