package com.abc.basic.algoritms.base.graph.test;


import com.abc.basic.algoritms.base.graph.DefaultDirectedGraph;
import com.abc.basic.algoritms.base.graph.DefaultEdge;
import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.traverse.BreadthFirstIterator;
import com.abc.basic.algoritms.base.graph.traverse.DepthFirstIterator;
import com.abc.basic.algoritms.base.graph.traverse.TopologicalOrderIterator;

import java.util.Iterator;

public class DirectedGraphMain {

    public static void main(String[] args) {
        DirectedGraph<String, DefaultEdge> dg = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

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
        ;
        String actual = "";

//        Iterator<String> bfs = new BreadthFirstIterator<>(dg);
//        while (bfs.hasNext()) {
//            String v = bfs.next();
//            actual += v;
//        }
//        System.out.println("BreadthFirstIterator->");
//        System.out.println(actual);

        Iterator<String> dfs = new DepthFirstIterator<>(dg);
        actual = "";
        while (dfs.hasNext()) {
            String v = dfs.next();
            actual += v;
        }
        //String expected = "ABCGIFEHJKLD";
        System.out.println("DepthFirstIterator->");
        System.out.println(actual);

//        Iterator<String> topfs = new TopologicalOrderIterator<>(dg);
//        actual = "";
//        while (topfs.hasNext()) {
//            String v = topfs.next();
//            actual += v;
//        }
//        System.out.println("TopologicalOrderIterator->");
//        System.out.println(actual);
    }
}
