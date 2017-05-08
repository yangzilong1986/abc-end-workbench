package com.abc.basic.algoritms.base.graph.test;


import com.abc.basic.algoritms.base.graph.DefaultWeightedEdge;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;
import com.abc.basic.algoritms.base.graph.SimpleWeightedGraph;
import com.abc.basic.algoritms.base.graph.spanning.BoruvkaMinimumSpanningTree;
import com.abc.basic.algoritms.base.graph.spanning.KruskalMinimumSpanningTree;
import com.abc.basic.algoritms.base.graph.spanning.PrimMinimumSpanningTree;
import com.abc.basic.algoritms.base.graph.spanning.SpanningTreeAlgorithm;
import junit.framework.*;
import org.jgrapht.generate.GnpRandomGraphGenerator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class MinimumSpanningTreeTest
        extends TestCase
{
    // ~ Static fields/initializers ---------------------------------------------

    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private static final String H = "H";

    // ~ Instance fields --------------------------------------------------------

    private DefaultWeightedEdge AB;
    private DefaultWeightedEdge AC;
    private DefaultWeightedEdge BD;
    private DefaultWeightedEdge DE;
    private DefaultWeightedEdge EG;
    private DefaultWeightedEdge GH;
    private DefaultWeightedEdge FH;

    // ~ Methods ----------------------------------------------------------------

    public void testKruskal()
    {

        KruskalMinimumSpanningTree<String, DefaultWeightedEdge> km= new KruskalMinimumSpanningTree<String, DefaultWeightedEdge>(
                createSimpleConnectedWeightedGraph());
        SpanningTreeAlgorithm.SpanningTree spTree=km.getSpanningTree();
        testMinimumSpanningTreeBuilding(spTree,
                Arrays.asList(AB, AC, BD, DE), 15.0);
        //Spanning-Tree [weight=15.0, edges=[(A : C), (D : E), (B : D), (A : B)]]

        km=new KruskalMinimumSpanningTree<String, DefaultWeightedEdge>(
                createSimpleDisconnectedWeightedGraph());
        spTree=km.getSpanningTree();
        //Spanning-Tree [weight=60.0, edges=[(G : H), (E : G), (A : C), (B : D), (A : B), (F : H)]]
        testMinimumSpanningTreeBuilding(spTree,
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);
    }

    public void testPrim()
    {
        PrimMinimumSpanningTree<String, DefaultWeightedEdge> prim=
                new PrimMinimumSpanningTree<String, DefaultWeightedEdge>(
                        createSimpleConnectedWeightedGraph());
        SpanningTreeAlgorithm.SpanningTree spanningTree=prim.getSpanningTree();
        //Spanning-Tree [weight=15.0, edges=[(A : C), (D : E), (A : B), (B : D)]]

        testMinimumSpanningTreeBuilding(spanningTree,
                Arrays.asList(AB, AC, BD, DE), 15.0);


        prim=new PrimMinimumSpanningTree<String, DefaultWeightedEdge>(
                createSimpleDisconnectedWeightedGraph());
        spanningTree=prim.getSpanningTree();
        //Spanning-Tree [weight=60.0, edges=[(B : D), (E : G), (A : C), (F : H), (A : B), (G : H)]]
        testMinimumSpanningTreeBuilding(spanningTree,
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);
    }

    public void testBoruvska()
    {
        BoruvkaMinimumSpanningTree<String, DefaultWeightedEdge> bm=
                new BoruvkaMinimumSpanningTree<String, DefaultWeightedEdge>(
                        createSimpleConnectedWeightedGraph());
        SpanningTreeAlgorithm.SpanningTree spanningTree=bm.getSpanningTree();
        //Spanning-Tree [weight=15.0, edges=[(A : B), (A : C), (B : D), (D : E)]]
        testMinimumSpanningTreeBuilding(spanningTree,
                Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
                new BoruvkaMinimumSpanningTree<String, DefaultWeightedEdge>(
                        createSimpleDisconnectedWeightedGraph()).getSpanningTree(),
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);
    }


    protected <V, E> void testMinimumSpanningTreeBuilding(
            final SpanningTreeAlgorithm.SpanningTree<E> mst, final Collection<E> edgeSet, final double weight)
    {
        assertEquals(weight, mst.getWeight());
        assertTrue(mst.getEdges().containsAll(edgeSet));
    }

    protected Graph<String, DefaultWeightedEdge> createSimpleDisconnectedWeightedGraph()
    {

        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        /**
         *
         * A -- B E -- F | | | | C -- D G -- H
         *
         */

        g.addVertex(A);
        g.addVertex(B);
        g.addVertex(C);
        g.addVertex(D);

        AB = Graphs.addEdge(g, A, B, 5);
        AC = Graphs.addEdge(g, A, C, 10);
        BD = Graphs.addEdge(g, B, D, 15);
        Graphs.addEdge(g, C, D, 20);

        g.addVertex(E);
        g.addVertex(F);
        g.addVertex(G);
        g.addVertex(H);

        Graphs.addEdge(g, E, F, 20);
        EG = Graphs.addEdge(g, E, G, 15);
        GH = Graphs.addEdge(g, G, H, 10);
        FH = Graphs.addEdge(g, F, H, 5);

        return g;
    }

    protected Graph<String, DefaultWeightedEdge> createSimpleConnectedWeightedGraph()
    {

        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        double bias = 1;

        g.addVertex(A);
        g.addVertex(B);
        g.addVertex(C);
        g.addVertex(D);
        g.addVertex(E);
        //([A, B, C, D, E], [{A,B}, {A,C}, {B,D}, {C,D}, {D,E}, {A,E}])
        AB = Graphs.addEdge(g, A, B, bias * 2);
        AC = Graphs.addEdge(g, A, C, bias * 3);
        BD = Graphs.addEdge(g, B, D, bias * 5);
        Graphs.addEdge(g, C, D, bias * 20);
        DE = Graphs.addEdge(g, D, E, bias * 5);
        Graphs.addEdge(g, A, E, bias * 100);

        return g;
    }

}

