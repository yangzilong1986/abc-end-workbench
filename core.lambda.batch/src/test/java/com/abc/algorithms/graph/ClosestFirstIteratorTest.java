package com.abc.algorithms.graph;


import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/**
 * Tests for ClosestFirstIterator.
 *
 * @author John V. Sichi
 * @since Sep 3, 2003
 */
public class ClosestFirstIteratorTest {

    public static void main(String[] args){
        closestFirstIterator();
    }

    public static void closestFirstIterator() {
        StringBuffer result = new StringBuffer();

        DirectedGraph<String, DefaultEdge> graph = createDirectedGraph();

        AbstractGraphIterator<String, ?> iterator = new ClosestFirstIterator<>(graph, "1", 301);

        while (iterator.hasNext()) {
            result.append(iterator.next());

            if (iterator.hasNext()) {
                result.append(',');
            }
        }
        String str=result.toString();
    }

    /**
     * .
     */
    public static void testNoStart() {
        StringBuffer result = new StringBuffer();

        DirectedGraph<String, DefaultEdge> graph = createDirectedGraph();

        AbstractGraphIterator<String, ?> iterator = new ClosestFirstIterator<>(graph);

        while (iterator.hasNext()) {
            result.append(iterator.next());

            if (iterator.hasNext()) {
                result.append(',');
            }
        }
        String str=result.toString();
    }


    static DirectedGraph<String, DefaultEdge> createDirectedGraph() {
        DirectedGraph<String, DefaultEdge> graph = null;

        //
        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "4";
        String v5 = "5";
        String v6 = "6";
        String v7 = "7";
        String v8 = "8";
        String v9 = "9";

        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex("3");
        graph.addVertex("4");
        graph.addVertex("5");
        graph.addVertex("6");
        graph.addVertex("7");
        graph.addVertex("8");
        graph.addVertex("9");

        graph.addVertex("orphan");

        // NOTE: set weights on some of the edges to test traversals like
        // ClosestFirstIterator where it matters. For other traversals, it
        // will be ignored. Rely on the default edge weight being 1.
        graph.addEdge(v1, v2);
        Graphs.addEdge(graph, v1, v3, 100);
        Graphs.addEdge(graph, v2, v4, 1000);
        graph.addEdge(v3, v5);
        Graphs.addEdge(graph, v3, v6, 100);
        graph.addEdge(v5, v6);
        Graphs.addEdge(graph, v5, v7, 200);
        graph.addEdge(v6, v1);
        Graphs.addEdge(graph, v7, v8, 100);
        graph.addEdge(v7, v9);
        graph.addEdge(v8, v2);
        graph.addEdge(v9, v4);

        return graph;
    }
}
