package com.abc.algorithms.graph;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 * Tests for the {@link DepthFirstIteratorTest} class.
 *
 * <p>
 * NOTE: This test uses hard-coded expected ordering isn't really guaranteed by the specification of
 * the algorithm. This could cause false failures if the traversal implementation changes.
 * </p>
 *
 * @author Liviu Rau
 * @since Jul 30, 2003
 */
public class DepthFirstIteratorTest
        //extends AbstractGraphIteratorTest
{
//    // ~ Methods ----------------------------------------------------------------
//
//    @Override
//    String getExpectedStr1()
//    {
//        return "1,3,6,5,7,9,4,8,2";
//    }
//
//    @Override
//    String getExpectedStr2()
//    {
//        return "1,3,6,5,7,9,4,8,2,orphan";
//    }
//
//    @Override
//    String getExpectedFinishString()
//    {
//        return "6:4:9:2:8:7:5:3:1:orphan:";
//    }
//
//    @Override
//    AbstractGraphIterator<String, DefaultWeightedEdge> createIterator(
//            Graph<String, DefaultWeightedEdge> g, String vertex)
//    {
//        AbstractGraphIterator<String, DefaultWeightedEdge> i = new DepthFirstIterator<>(g, vertex);
//        i.setCrossComponentTraversal(true);
//
//        return i;
//    }

    public static void main(String[] args){
        depthFirstIterator();
    }
    public static void depthFirstIterator()
    {
        Graph<String, DefaultEdge> dg = new DefaultDirectedGraph<>(DefaultEdge.class);

        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String e = "E";
        String f = "F";
        String g = "G";
        String h = "H";
        String i = "I";
        String j = "J";
        String k = "K";
        String l = "L";

        dg.addVertex(a);
        dg.addVertex(b);
        dg.addVertex(c);
        dg.addVertex(d);
        dg.addVertex(e);
        dg.addVertex(f);
        dg.addVertex(g);
        dg.addVertex(h);
        dg.addVertex(i);
        dg.addVertex(j);
        dg.addVertex(k);
        dg.addVertex(l);

        dg.addEdge(a, b);
        dg.addEdge(b, c);
        dg.addEdge(c, j);
        dg.addEdge(c, d);
        dg.addEdge(c, e);
        dg.addEdge(c, f);
        dg.addEdge(c, g);
        dg.addEdge(d, h);
        dg.addEdge(e, h);
        dg.addEdge(f, i);
        dg.addEdge(g, i);
        dg.addEdge(h, j);
        dg.addEdge(i, c);
        dg.addEdge(j, k);
        dg.addEdge(k, l);

        Iterator<String> dfs = new DepthFirstIterator<>(dg);
        String actual = "";
        while (dfs.hasNext()) {
            String v = dfs.next();
            actual += v;
        }

        String expected = "ABCGIFEHJKLD";
//        assertEquals(expected, actual);
    }
}

