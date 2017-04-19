package com.abc.algorithms.base.introduction;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public abstract class Graph<T> {
    protected int[][] adjacencyMatrix;// 邻接矩阵
    protected int size = 0;// 定点总数
    public Set<Vertex<T>> vertices;// 所有节点
    public Set<Edge<T>> edges;// 所有边
    private int time;
    private Stack<Vertex<T>> topologicalStack;//用于拓扑排序
    public Graph() {
        // TODO Auto-generated constructor stub
        adjacencyMatrix = new int[size][size];
        vertices = new LinkedHashSet<Vertex<T>>();
        edges = new LinkedHashSet<Edge<T>>();
        topologicalStack=new Stack<Vertex<T>>();
    }

    public Graph<T> addVertex(T t) {
        Vertex<T> vertex = new Vertex<T>(t);
        // 如果已经包含该节点，抛出异常
        if (vertices.contains(vertex)) {
            throw new IllegalArgumentException("该节点已经存在，不能再添加！");
        }
        size++;
        vertices.add(vertex);
        adjustMatrix(t);
        return this;
    }

    // 添加边，t1-t2
    public abstract void addEdge(T t1, T t2);

    // 调整邻接矩阵
    private void adjustMatrix(T t) {
        int[][] tempyMatrix = new int[size][size];
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                tempyMatrix[i][j] = adjacencyMatrix[i][j];
            }
        }
        adjacencyMatrix = tempyMatrix;
    }

    protected void setMatrixValue(int row, int column) {
        adjacencyMatrix[row][column] = 1;
    }

    // 以邻接矩阵的形式打印Graph
    public void printAdjacencyMatrix() {
        System.out.println("邻接矩阵形式：");
        for (int[] is : adjacencyMatrix) {
            for (int i : is) {
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // 以邻接表的形式打印Graph
    public void printAdjacencyVertices() {
        System.out.println("邻接表形式：");
        for (Vertex<T> vertex : vertices) {
            System.out.print(vertex);
            for (Vertex<T> vertex2 : vertex.adjacencyVertices) {
                System.out.print("→");
                System.out.print(vertex2);
            }
            System.out.println();
        }
    }

    protected Vertex<T> isContainVertext(T t) {
        for (Vertex<T> v : vertices) {
            if (v.value.equals(t)) {
                return v;
            }
        }
        return null;
    }

    protected Edge<T> isContainEdge(T t1, T t2) {
        for (Edge<T> edge : edges) {
            if (edge.source.equals(t1) && edge.target.equals(t2)) {
                return edge;
            }
        }
        return null;
    }

    // 广度优先搜索,广度优先搜索其实是利用了一个队列
    // 参考《算法导论》22.2节伪代码
    public void printBFS(T t) {
        Vertex<T> vertex = isContainVertext(t);
        if (vertex == null) {
            throw new IllegalArgumentException("不能包含该节点，不能进行广度优先搜索！");
        }
        reSet();
        System.out.println("广度优先搜索：");
        vertex.color = Color.GRAY;
        vertex.distance = 0;
        Queue<Vertex<T>> queue = new LinkedList<Vertex<T>>();
        queue.offer(vertex);
        while (queue.size() > 0) {
            Vertex<T> u = queue.poll();
            for (Vertex<T> v : u.adjacencyVertices) {
                if (v.color == Color.WHITE) {
                    v.color = Color.GRAY;
                    v.distance = u.distance + 1;
                    v.parent = u;
                    queue.offer(v);
                }
            }
            u.color = Color.BLACK;
            System.out.print(u);
            System.out.print("→");
        }
        System.out.println();
    }
    public void printDFS(T t){
        Vertex<T> vertex = isContainVertext(t);
        if (vertex == null) {
            throw new IllegalArgumentException("不能包含该节点，不能进行广度优先搜索！");
        }
        reSet();
        System.out.println("深度优先搜索：");
        time=0;
        dfsVisit(vertex);
        for (Vertex<T> u : vertices) {
            if (u.equals(vertex)) {
                continue;
            }
            if (u.color==Color.WHITE) {
                dfsVisit(u);
            }
        }
        System.out.println();
    }
    private void dfsVisit(Vertex<T> u){
        System.out.print(u);
        System.out.print("→");
        time++;
        u.discover=time;
        u.color=Color.GRAY;
        for (Vertex<T> v : u.adjacencyVertices) {
            if (v.color==Color.WHITE) {
                v.parent=u;
                dfsVisit(v);
            }
        }
        u.color=Color.BLACK;
        time++;
        u.finish=time;

        topologicalStack.push(u);

    }
    /**
     * 打印出拓扑结构
     * */
    public void printTopology(T t){
        if (topologicalStack.size()==0) {
            printDFS(t);
        }
        while (topologicalStack.size()>0) {
            System.out.print(topologicalStack.pop());
            System.out.print("→");
        }
        System.out.println();

    }
    private void reSet(){
        for (Vertex<T> vertex : vertices) {
            vertex.color=Color.WHITE;
        }
        topologicalStack.clear();
    }

}
