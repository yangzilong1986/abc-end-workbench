package com.abc.basic.algoritms.base.graph;

import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

import java.util.*;
import java.util.function.*;

public abstract class Graphs
{


    public static <V, E> E addEdge(Graph<V, E> g, V sourceVertex, V targetVertex, double weight)
    {
        EdgeFactory<V, E> ef = g.getEdgeFactory();
        E e = ef.createEdge(sourceVertex, targetVertex);

        // we first create the edge and set the weight to make sure that
        // listeners will see the correct weight upon addEdge.

        assert (g instanceof WeightedGraph<?, ?>) : g.getClass();
        ((WeightedGraph<V, E>) g).setEdgeWeight(e, weight);

        return g.addEdge(sourceVertex, targetVertex, e) ? e : null;
    }


    public static <V, E> E addEdgeWithVertices(Graph<V, E> g, V sourceVertex, V targetVertex)
    {
        g.addVertex(sourceVertex);
        g.addVertex(targetVertex);

        return g.addEdge(sourceVertex, targetVertex);
    }

    public static <V,
            E> boolean addEdgeWithVertices(Graph<V, E> targetGraph,Graph<V, E> sourceGraph, E edge)
    {
        V sourceVertex = sourceGraph.getEdgeSource(edge);
        V targetVertex = sourceGraph.getEdgeTarget(edge);

        targetGraph.addVertex(sourceVertex);
        targetGraph.addVertex(targetVertex);

        return targetGraph.addEdge(sourceVertex, targetVertex, edge);
    }


//    public static <V,
//            E> E addEdgeWithVertices(Graph<V, E> g, V sourceVertex, V targetVertex, double weight)
//    {
//        g.addVertex(sourceVertex);
//        g.addVertex(targetVertex);
//
//        return addEdge(g, sourceVertex, targetVertex, weight);
//    }

    public static <V,
            E> boolean addGraph(Graph<? super V, ? super E> destination,Graph<V, E> source)
    {
        boolean modified = addAllVertices(destination, source.vertexSet());
        modified |= addAllEdges(destination, source, source.edgeSet());

        return modified;
    }


    public static <V, E> void addGraphReversed(
           DirectedGraph<? super V, ? super E> destination, DirectedGraph<V, E> source)
    {
        addAllVertices(destination, source.vertexSet());

        for (E edge : source.edgeSet()) {
            destination.addEdge(source.getEdgeTarget(edge), source.getEdgeSource(edge));
        }
    }


    public static <V, E> boolean addAllEdges(
            Graph<? super V, ? super E> destination, Graph<V, E> source, Collection<? extends E> edges)
    {
        boolean modified = false;

        for (E e : edges) {
            V s = source.getEdgeSource(e);
            V t = source.getEdgeTarget(e);
            destination.addVertex(s);
            destination.addVertex(t);
            modified |= destination.addEdge(s, t, e);
        }

        return modified;
    }

    public static <V, E> boolean addAllVertices(
            Graph<? super V, ? super E> destination, Collection<? extends V> vertices)
    {
        boolean modified = false;

        for (V v : vertices) {
            modified |= destination.addVertex(v);
        }

        return modified;
    }

    public static <V, E> List<V> neighborListOf(Graph<V, E> g, V vertex)
    {
        List<V> neighbors = new ArrayList<>();

        for (E e : g.edgesOf(vertex)) {
            neighbors.add(getOppositeVertex(g, e, vertex));
        }

        return neighbors;
    }


    public static <V, E> List<V> predecessorListOf(DirectedGraph<V, E> g, V vertex)
    {
        List<V> predecessors = new ArrayList<>();
        Set<? extends E> edges = g.incomingEdgesOf(vertex);

        for (E e : edges) {
            predecessors.add(getOppositeVertex(g, e, vertex));
        }

        return predecessors;
    }

    public static <V, E> List<V> successorListOf(DirectedGraph<V, E> g, V vertex)
    {
        List<V> successors = new ArrayList<>();
        Set<? extends E> edges = g.outgoingEdgesOf(vertex);

        for (E e : edges) {
            successors.add(getOppositeVertex(g, e, vertex));
        }

        return successors;
    }


//    public static <V, E> UndirectedGraph<V, E> undirectedGraph(org.jgrapht.Graph<V, E> g)
//    {
//        if (g instanceof org.jgrapht.DirectedGraph<?, ?>) {
//            return new AsUndirectedGraph<>((org.jgrapht.DirectedGraph<V, E>) g);
//        } else if (g instanceof UndirectedGraph<?, ?>) {
//            return (UndirectedGraph<V, E>) g;
//        } else {
//            throw new IllegalArgumentException(
//                    "Graph must be either DirectedGraph or UndirectedGraph");
//        }
//    }

    /**
     * Tests whether an edge is incident to a vertex.
     *
     * @param g graph containing e and v
     * @param e edge in g
     * @param v vertex in g
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return true iff e is incident on v
     */
    public static <V, E> boolean testIncidence(Graph<V, E> g, E e, V v)
    {
        return (g.getEdgeSource(e).equals(v)) || (g.getEdgeTarget(e).equals(v));
    }

    public static <V, E> V getOppositeVertex(Graph<V, E> g, E e, V v)
    {
        V source = g.getEdgeSource(e);
        V target = g.getEdgeTarget(e);
        if (v.equals(source)) {//通过顶点获取另顶点
            return target;
        } else if (v.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException("no such vertex: " + v.toString());
        }
    }

    public static <V,
            E> boolean removeVertexAndPreserveConnectivity(DirectedGraph<V, E> graph, V vertex)
    {

        if (!graph.containsVertex(vertex)) {
            return false;
        }

        if (vertexHasPredecessors(graph, vertex)) {
            List<V> predecessors =predecessorListOf(graph, vertex);
            List<V> successors = successorListOf(graph, vertex);

            for (V predecessor : predecessors) {
                addOutgoingEdges(graph, predecessor, successors);
            }
        }

        graph.removeVertex(vertex);
        return true;
    }

    public static <V, E> boolean removeVerticesAndPreserveConnectivity(
           DirectedGraph<V, E> graph, Predicate<V> predicate)
    {

        List<V> verticesToRemove = new ArrayList<>();

        for (V node : graph.vertexSet()) {
            if (predicate.test(node)) {
                verticesToRemove.add(node);
            }
        }

        return removeVertexAndPreserveConnectivity(graph, verticesToRemove);
    }

    public static <V, E> boolean removeVertexAndPreserveConnectivity(
            DirectedGraph<V, E> graph, Iterable<V> vertices)
    {

        boolean atLeastOneVertexHasBeenRemoved = false;

        for (V vertex : vertices) {
            if (removeVertexAndPreserveConnectivity(graph, vertex)) {
                atLeastOneVertexHasBeenRemoved = true;
            }
        }

        return atLeastOneVertexHasBeenRemoved;
    }

    public static <V,
            E> void addOutgoingEdges(DirectedGraph<V, E> graph, V source, Iterable<V> targets)
    {
        if (!graph.containsVertex(source)) {
            graph.addVertex(source);
        }
        for (V target : targets) {
            if (!graph.containsVertex(target)) {
                graph.addVertex(target);
            }
            graph.addEdge(source, target);
        }
    }

    public static <V,
            E> void addIncomingEdges(DirectedGraph<V, E> graph, V target, Iterable<V> sources)
    {
        if (!graph.containsVertex(target)) {
            graph.addVertex(target);
        }
        for (V source : sources) {
            if (!graph.containsVertex(source)) {
                graph.addVertex(source);
            }
            graph.addEdge(source, target);
        }
    }


    public static <V, E> boolean vertexHasSuccessors(DirectedGraph<V, E> graph, V vertex)
    {
        return !graph.outgoingEdgesOf(vertex).isEmpty();
    }


    public static <V, E> boolean vertexHasPredecessors(DirectedGraph<V, E> graph, V vertex)
    {
        return !graph.incomingEdgesOf(vertex).isEmpty();
    }
}

