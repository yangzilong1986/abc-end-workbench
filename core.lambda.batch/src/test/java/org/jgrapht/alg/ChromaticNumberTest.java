package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 *
 * 完全图
 *
 *若一个图的每一对不同顶点恰有一条边相连，则称为完全图。完全图是每对顶点之间都恰连有一条边的简单图。
 * n个端点的完全图有n个端点及n(n − 1) / 2条边，以Kn表示。它是(k − 1)-正则图。所有完全图都是它本身的团（clique）。
 */
public class ChromaticNumberTest
    extends TestCase
{
    // ~ Methods ----------------------------------------------------------------

    /**
     * .
     */
    public void testChromaticNumber()
    {
        UndirectedGraph<Object, DefaultEdge> completeGraph = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Object, DefaultEdge> completeGenerator =
            new CompleteGraphGenerator<>(7);
        completeGenerator
            .generateGraph(completeGraph, new ClassBasedVertexFactory<>(Object.class), null);

        // A complete graph has a chromatic number equal to its order
        assertEquals(7, ChromaticNumber.findGreedyChromaticNumber(completeGraph));
        Map<Integer, Set<Object>> coloring = ChromaticNumber.findGreedyColoredGroups(completeGraph);
        assertEquals(7, coloring.keySet().size());

        UndirectedGraph<Object, DefaultEdge> linearGraph = new SimpleGraph<>(DefaultEdge.class);
        LinearGraphGenerator<Object, DefaultEdge> linearGenerator = new LinearGraphGenerator<>(50);
        linearGenerator
            .generateGraph(linearGraph, new ClassBasedVertexFactory<>(Object.class), null);

        // A linear graph is a tree, and a greedy algorithm for chromatic number
        // can always find a 2-coloring
        assertEquals(2, ChromaticNumber.findGreedyChromaticNumber(linearGraph));
    }
}

// End ChromaticNumberTest.java
