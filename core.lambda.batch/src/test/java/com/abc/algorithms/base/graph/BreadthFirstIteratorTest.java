package com.abc.algorithms.base.graph;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.Iterator;

public class BreadthFirstIteratorTest
        extends AbstractGraphIteratorTest
{
    // ~ Methods ----------------------------------------------------------------

    @Override
    String getExpectedStr1()
    {
        return "1,2,3,4,5,6,7,8,9";
    }

    @Override
    String getExpectedStr2()
    {
        return "1,2,3,4,5,6,7,8,9,orphan";
    }

    @Override
    AbstractGraphIterator<String, DefaultWeightedEdge> createIterator(
            Graph<String, DefaultWeightedEdge> g, String vertex)
    {
        AbstractGraphIterator<String, DefaultWeightedEdge> i =
                new BreadthFirstIterator<>(g, vertex);
        i.setCrossComponentTraversal(true);

        return i;
    }

    public void testBreadthFirst(){
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
        //广度优先使用先进选出队列完成
        Iterator<String> dfs = new BreadthFirstIterator<>(dg);
        String actual = "";
        while (dfs.hasNext()) {
            String v = dfs.next();
            actual += v;
        }
        System.out.println("广度优先遍历");
        System.out.println(actual);
    }
}

