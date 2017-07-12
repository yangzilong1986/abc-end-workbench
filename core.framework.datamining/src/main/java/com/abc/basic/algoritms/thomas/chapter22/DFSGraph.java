package com.abc.basic.algoritms.thomas.chapter22;

import java.util.*;

/**
 * 图的深度（Depth-first search）优先算法以及拓扑排序算法。
 */
public class DFSGraph {
    private static final int MAX_VERTEX = 20;

    private int nextId;
    //边由Node的的另一个顶点表示为边
    private Edge[] edges;
    private int vertexCount;

    public DFSGraph(int maxVertex) {

        edges = new Edge[maxVertex];
    }

    public DFSGraph() {
        this(MAX_VERTEX);
    }

    int time = 0;

    public List<Vertex> DFS() {
        for (int i = 0; i < vertexCount; i++) {
            edges[i].vertex.color = Vertex.WHITE;
            edges[i].vertex.parent = null;
        }
        time = 0;
        List<Vertex> result = new ArrayList<Vertex>();
        for (int i = 0; i < vertexCount; i++) {
            Edge u = edges[i];
            if (u.vertex.color == Vertex.WHITE) {
                dfsVisit(u, result);
            }
        }
        return result;
    }

    private void dfsVisit(Edge u, List<Vertex> vertexes) {
        vertexes.add(u.vertex);
        u.vertex.color = Vertex.GRAY;
        time++;
        u.vertex.start = time;
        for (Edge v = edges[u.vertex.id].otherVertex; v != null; v = v.otherVertex) {
            if (v.vertex.color == Vertex.WHITE) {
                v.vertex.parent = u.vertex;
                dfsVisit(v, vertexes);
            }
        }
        u.vertex.color = Vertex.BLACK;
        time++;
        u.vertex.finish = time;
    }

    public static void printDFSResult(List<Vertex> vertexes) {
        for (int i = 0; i < vertexes.size(); i++) {
            Vertex v = vertexes.get(i);
            System.out.printf("Vertex %s: parent=%s, start=%d, finish=%d%n", v, v.parent, v.start, v.finish);
        }
    }

    public List<Vertex> toplogicalSort() {
        for (int i = 0; i < vertexCount; i++) {
            edges[i].vertex.color = Vertex.WHITE;
            edges[i].vertex.parent = null;
        }
        time = 0;
        LinkedList<Vertex> result = new LinkedList<Vertex>();
        for (int i = 0; i < vertexCount; i++) {
            Edge u = edges[i];
            if (u.vertex.color == Vertex.WHITE) {
                dfsVisit2(u, result);
            } else if (u.vertex.color == Vertex.GRAY) {
                throw new IllegalStateException("a circular has detected.");
            }
        }
        return result;
    }

    private void dfsVisit2(Edge u, LinkedList<Vertex> vertexes) {
        u.vertex.color = Vertex.GRAY;
        time++;
        u.vertex.start = time;
        for (Edge v = edges[u.vertex.id].otherVertex; v != null; v = v.otherVertex) {
            if (v.vertex.color == Vertex.WHITE) {
                v.vertex.parent = u.vertex;
                dfsVisit2(v, vertexes);
            } else if (v.vertex.color == Vertex.GRAY) {
                throw new IllegalStateException("a circular has detected.");
            }
        }
        u.vertex.color = Vertex.BLACK;
        time++;
        u.vertex.finish = time;
        vertexes.addFirst(u.vertex);
    }

    private int nextId() {
        return nextId++;
    }

    public Vertex addVertex(String data) {
        int id = nextId();
        //顶点和边的关系定义在Node中，
        // 用id做索引
        //edges[id]首先创建了一个顶点，另一个顶点在创建边时加入
        edges[id] = new Edge(new Vertex(id, data));
        vertexCount++;

        return edges[id].vertex;
    }

    public void addEdge(Vertex v1, Vertex v2) {
        assert v1 == edges[v1.id].vertex;
        assert v2 == edges[v2.id].vertex;
        //另一个顶点
        Edge newNode = new Edge(v2);
        //从顶点v1中获取已经连接的顶点，即边，当时一个边时，otherVertext为null
        newNode.otherVertex = edges[v1.id].otherVertex;//
        //顶点v的另一个顶点为newNode,即重设其另一个顶点
        edges[v1.id].otherVertex = newNode;
    }

    public void addUndirectionalEdge(Vertex v1, Vertex v2) {
        addEdge(v1, v2);
        addEdge(v2, v1);
    }

    private static class Edge {
        //顶点
        Vertex vertex;
        //另一个边
        Edge otherVertex;

        public Edge(Vertex v) {
            vertex = v;
        }

        public String toString() {
            return vertex.toString();
        }
    }

    /**
     * 顶点
     */
    public static class Vertex {
        public static final int WHITE = 0;
        public static final int GRAY = 1;
        public static final int BLACK = 2;

        int id;
        String data;
        Vertex parent;
        int start;
        int finish;
        int color;

        public Vertex(int id, String data) {
            this.id = id;
            this.data = data;
        }

        public String toString() {
            return (data);
        }
    }

    public static void main(String[] args) {
        DFSGraph graph = new DFSGraph();
        Vertex w = graph.addVertex("w");

        Vertex u = graph.addVertex("u");
        Vertex v = graph.addVertex("v");

        Vertex x = graph.addVertex("x");
        Vertex y = graph.addVertex("y");
        Vertex z = graph.addVertex("z");
        graph.addEdge(w, y);
        graph.addEdge(w, z);

        graph.addEdge(y, x);
//		graph.addEdge(u, x);
        graph.addEdge(x, u);
        graph.addEdge(x, v);
//        graph.addEdge(v, y);
        graph.addEdge(y, v);
        graph.addEdge(z, z);

        List<Vertex> vertexes = null;
        vertexes = graph.DFS();
        System.out.println("============vertexes==============");
        printDFSResult(vertexes);

        System.out.println("==========================");
        graph = new DFSGraph();
        Vertex s = graph.addVertex("s");
        u = graph.addVertex("u");
        Vertex t = graph.addVertex("t");
        v = graph.addVertex("v");
        w = graph.addVertex("w");
        x = graph.addVertex("x");
        y = graph.addVertex("y");
        z = graph.addVertex("z");

        graph.addEdge(s, z);
        graph.addEdge(s, w);
        graph.addEdge(t, v);
        graph.addEdge(t, u);
        graph.addEdge(u, v);
        graph.addEdge(u, t);
        graph.addEdge(v, w);
        graph.addEdge(v, s);
        graph.addEdge(w, x);
        graph.addEdge(x, z);
        graph.addEdge(y, x);
        graph.addEdge(z, w);
        graph.addEdge(z, y);
        vertexes = graph.DFS();
        printDFSResult(vertexes);

        System.out.println("==========================");
        graph = new DFSGraph();
        Vertex undershorts = graph.addVertex("undershorts");
        Vertex socks = graph.addVertex("socks");
        /*Vertex watch = */
        graph.addVertex("watch");
        Vertex pants = graph.addVertex("pants");
        Vertex shoes = graph.addVertex("shoes");
        Vertex belt = graph.addVertex("belt");
        Vertex shirt = graph.addVertex("shirt");
        Vertex tie = graph.addVertex("tie");
        Vertex jacket = graph.addVertex("jacket");
        graph.addEdge(undershorts, pants);
        graph.addEdge(undershorts, shoes);
        graph.addEdge(socks, shoes);
        graph.addEdge(pants, shoes);
        graph.addEdge(pants, belt);
        graph.addEdge(shirt, belt);
        graph.addEdge(shirt, tie);
        graph.addEdge(tie, jacket);
        graph.addEdge(belt, jacket);

        vertexes = graph.toplogicalSort();
        printDFSResult(vertexes);


        System.out.println("==========================");
        // 练习22.4-1
        graph = new DFSGraph();
        Vertex m = graph.addVertex("m");
        Vertex n = graph.addVertex("n");
        Vertex o = graph.addVertex("o");
        Vertex p = graph.addVertex("p");
        Vertex q = graph.addVertex("q");
        Vertex r = graph.addVertex("r");
        s = graph.addVertex("s");
        t = graph.addVertex("t");
        u = graph.addVertex("u");
        v = graph.addVertex("v");
        w = graph.addVertex("w");
        x = graph.addVertex("x");
        y = graph.addVertex("y");
        z = graph.addVertex("z");

        graph.addEdge(m, x);
        graph.addEdge(m, r);
        graph.addEdge(m, q);
        graph.addEdge(n, u);
        graph.addEdge(n, q);
        graph.addEdge(n, o);
        graph.addEdge(o, v);
        graph.addEdge(o, s);
        graph.addEdge(o, r);
        graph.addEdge(p, z);
        graph.addEdge(p, o);
        graph.addEdge(q, t);
        graph.addEdge(r, y);
        graph.addEdge(r, u);
        graph.addEdge(s, r);
        graph.addEdge(u, t);
        graph.addEdge(v, x);
        graph.addEdge(v, w);
        graph.addEdge(w, z);
        graph.addEdge(y, v);
        vertexes = graph.toplogicalSort();
        printDFSResult(vertexes);
    }
}
