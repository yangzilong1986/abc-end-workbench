package com.abc.basic.algoritms.base.graph.scc;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.graph.DirectedSubgraph;

import java.util.*;

public class GabowStrongConnectivityInspector<V, E>
        implements StrongConnectivityAlgorithm<V, E>
{
    // the graph to compute the strongly connected sets
    private final DirectedGraph<V, E> graph;

    // stores the vertices
    private Deque<VertexNumber<V>> stack = new ArrayDeque<>();

    // the result of the computation, cached for future calls
    private List<Set<V>> stronglyConnectedSets;

    // the result of the computation, cached for future calls
    private List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

    // maps vertices to their VertexNumber object
    private Map<V, VertexNumber<V>> vertexToVertexNumber;

    // store the numbers
    private Deque<Integer> B = new ArrayDeque<>();

    // number of vertices
    private int c;

    public GabowStrongConnectivityInspector(DirectedGraph<V, E> directedGraph)
    {
        if (directedGraph == null) {
            throw new IllegalArgumentException("null not allowed for graph!");
        }

        graph = directedGraph;
        vertexToVertexNumber = null;

        stronglyConnectedSets = null;
    }

    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    public boolean isStronglyConnected()
    {
        return stronglyConnectedSets().size() == 1;
    }

    public List<Set<V>> stronglyConnectedSets()
    {
        if (stronglyConnectedSets == null) {
            stronglyConnectedSets = new Vector<>();

            // create VertexData objects for all vertices, store them
            createVertexNumber();

            // perform DFS
            for (VertexNumber<V> data : vertexToVertexNumber.values()) {
                if (data.getNumber() == 0) {
                    dfsVisit(graph, data);
                }
            }

            vertexToVertexNumber = null;
            stack = null;
            B = null;
        }

        return stronglyConnectedSets;
    }

    public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs()
    {
        if (stronglyConnectedSubgraphs == null) {
            List<Set<V>> sets = stronglyConnectedSets();
            stronglyConnectedSubgraphs = new Vector<>(sets.size());

            for (Set<V> set : sets) {
                stronglyConnectedSubgraphs.add(new DirectedSubgraph<>(graph, set, null));
            }
        }

        return stronglyConnectedSubgraphs;
    }

    private void createVertexNumber()
    {
        c = graph.vertexSet().size();
        vertexToVertexNumber = new HashMap<>(c);

        for (V vertex : graph.vertexSet()) {
            vertexToVertexNumber.put(vertex, new VertexNumber<>(vertex, 0));
        }

        stack = new ArrayDeque<>(c);
        B = new ArrayDeque<>(c);
    }

    /*
     * The subroutine of DFS.
     */
    private void dfsVisit(DirectedGraph<V, E> visitedGraph, VertexNumber<V> v)
    {
        VertexNumber<V> w;
        stack.add(v);
        B.add(v.setNumber(stack.size() - 1));

        // follow all edges

        for (E edge : visitedGraph.outgoingEdgesOf(v.getVertex())) {
            w = vertexToVertexNumber.get(visitedGraph.getEdgeTarget(edge));

            if (w.getNumber() == 0) {
                dfsVisit(graph, w);
            } else { /* contract if necessary */
                while (w.getNumber() < B.getLast()) {
                    B.removeLast();
                }
            }
        }
        Set<V> L = new HashSet<>();
        if (v.getNumber() == (B.getLast())) {
            /*
             * number vertices of the next strong component
             */
            B.removeLast();

            c++;
            while (v.getNumber() <= (stack.size() - 1)) {
                VertexNumber<V> r = stack.removeLast();
                L.add(r.getVertex());
                r.setNumber(c);
            }
            stronglyConnectedSets.add(L);
        }
    }

    private static final class VertexNumber<V>
    {
        V vertex;
        int number = 0;

        private VertexNumber(V vertex, int number)
        {
            this.vertex = vertex;
            this.number = number;
        }

        int getNumber()
        {
            return number;
        }

        V getVertex()
        {
            return vertex;
        }

        Integer setNumber(int n)
        {
            return number = n;
        }
    }
}

