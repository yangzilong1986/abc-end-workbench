package com.abc.algorithms.base.introduction;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        testUndirectedGraph();
        testDirectedGraph();
        testBFS();
        testDFS();
        testTopolgy();
    }
    /**
     * 《算法导论》22.1节图22.1
     * */
    private static void testUndirectedGraph(){
        Graph<String> graph=new UndirectedGraph<String>();
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");
        graph.addVertex("5");
        graph.addEdge("1","2");
        graph.addEdge("5","1");
        graph.addEdge("2","3");
        graph.addEdge("2","4");
        graph.addEdge("2","5");
        graph.addEdge("4","3");
        graph.addEdge("4","5");

        graph.printAdjacencyMatrix();
        graph.printAdjacencyVertices();
    }
    /**
     * 《算法导论》22.1节图22.1
     * */
    private static void testDirectedGraph(){
        Graph<Integer> graph2=new DirectedGraph<Integer>();
        graph2.addVertex(1).addVertex(2).addVertex(3).addVertex(4).addVertex(5).addVertex(6);
        graph2.addEdge(1, 2);
        graph2.addEdge(1, 4);
        graph2.addEdge(2, 5);
        graph2.addEdge(3, 5);
        graph2.addEdge(3, 6);
        graph2.addEdge(4, 2);
        graph2.addEdge(5, 4);
        graph2.addEdge(6, 6);
        graph2.printAdjacencyMatrix();
        graph2.printAdjacencyVertices();
    }
    /**
     * 《算法导论》22.2节图22.3
     *
     * */
    private static void testBFS(){
        Graph<String> graph=new UndirectedGraph<String>();
        graph.addVertex("s").addVertex("r").addVertex("v")
                .addVertex("w").addVertex("t").addVertex("x").addVertex("u").addVertex("y");

        graph.addEdge("v","r");
        graph.addEdge("r","s");
        graph.addEdge("s","w");
        graph.addEdge("w","t");
        graph.addEdge("w","x");
        graph.addEdge("t","x");
        graph.addEdge("t","u");
        graph.addEdge("x","u");
        graph.addEdge("x","y");
        graph.addEdge("u","y");
        graph.printBFS("x");

    }
    private static void testDFS(){
        Graph<String> graph2=new DirectedGraph<String>();
        graph2.addVertex("u").addVertex("v").addVertex("w").addVertex("x").addVertex("y").addVertex("z");
        graph2.addEdge("u", "x");
        graph2.addEdge("u", "v");
        graph2.addEdge("x", "v");
        graph2.addEdge("v", "y");
        graph2.addEdge("y", "x");
        graph2.addEdge("w", "y");
        graph2.addEdge("w", "z");
        graph2.addEdge("z", "z");
        graph2.printDFS("u");

    }
    /**
     * 《算法导论》22.4节图22.7
     *
     * */
    private static void testTopolgy(){
        Graph<String> graph2=new DirectedGraph<String>();
        graph2.addVertex("undershorts").addVertex("pants").addVertex("belt").
                addVertex("shirt").addVertex("tie").addVertex("jacket").addVertex("socks").
                addVertex("watch").addVertex("shoes");
        graph2.addEdge("undershorts", "pants");
        graph2.addEdge("undershorts", "shoes");
        graph2.addEdge("pants", "belt");
        graph2.addEdge("belt", "jacket");
        graph2.addEdge("pants", "shoes");
        graph2.addEdge("shirt", "tie");
        graph2.addEdge("shirt", "belt");
        graph2.addEdge("tie", "jacket");
        graph2.addEdge("socks", "shoes");
        graph2.printTopology("shirt");
    }

}

