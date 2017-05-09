package com.abc.basic.algoritms.base.graph.test;

import com.abc.basic.algoritms.base.graph.DefaultDirectedGraph;
import com.abc.basic.algoritms.base.graph.DefaultEdge;
import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.cycle.*;
import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;

import java.util.List;

public class DirectedSimpleCyclesTest
{
    private static int MAX_SIZE = 9;
    private static int[] RESULTS = { 0, 1, 3, 8, 24, 89, 415, 2372, 16072, 125673 };


    public static void main(String[] args){
        TiernanSimpleCycles<Integer, DefaultEdge> tiernanFinder = new TiernanSimpleCycles<>();
        TarjanSimpleCycles<Integer, DefaultEdge> tarjanFinder = new TarjanSimpleCycles<>();
        JohnsonSimpleCycles<Integer, DefaultEdge> johnsonFinder = new JohnsonSimpleCycles<>();
        SzwarcfiterLauerSimpleCycles<Integer, DefaultEdge> szwarcfiterLauerFinder =
                new SzwarcfiterLauerSimpleCycles<>();
        HawickJamesSimpleCycles<Integer, DefaultEdge> hawickJamesFinder =
                new HawickJamesSimpleCycles<>();

        testAlgorithm(johnsonFinder);

        testAlgorithm(tiernanFinder);
        testAlgorithm(tarjanFinder);

        testAlgorithm(szwarcfiterLauerFinder);
        testAlgorithm(hawickJamesFinder);
    }

    private static void testAlgorithm(DirectedSimpleCycles<Integer, DefaultEdge> finder)
    {
        DirectedGraph<Integer, DefaultEdge> graph =
                new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(DefaultEdge.class));
        for (int i = 0; i < 7; i++) {
            graph.addVertex(i);
        }
        finder.setGraph(graph);
        graph.addEdge(0, 0);
        checkResult(finder, 1);
        graph.addEdge(1, 1);
        checkResult(finder, 2);
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        checkResult(finder, 3);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        checkResult(finder, 4);
        graph.addEdge(6, 6);
        checkResult(finder, 5);

        for (int size = 1; size <= MAX_SIZE; size++) {
            graph = new DefaultDirectedGraph<>(new ClassBasedEdgeFactory<>(DefaultEdge.class));
            for (int i = 0; i < size; i++) {
                graph.addVertex(i);
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    graph.addEdge(i, j);
                }
            }
            finder.setGraph(graph);
            checkResult(finder, RESULTS[size]);
        }
    }

    private static void checkResult(DirectedSimpleCycles<Integer, DefaultEdge> finder, int size)
    {
        List<List<Integer>> list=finder.findSimpleCycles();
        System.out.println("over...");
    }
}
