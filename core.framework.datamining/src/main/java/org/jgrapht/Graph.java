package org.jgrapht;

import java.util.*;

public interface Graph<V, E>
{
    Set<E> getAllEdges(V sourceVertex, V targetVertex);


    E getEdge(V sourceVertex, V targetVertex);


    EdgeFactory<V, E> getEdgeFactory();

    E addEdge(V sourceVertex, V targetVertex);

    boolean addEdge(V sourceVertex, V targetVertex, E e);

    boolean addVertex(V v);

    boolean containsEdge(V sourceVertex, V targetVertex);

    boolean containsEdge(E e);

    boolean containsVertex(V v);

    Set<E> edgeSet();

    Set<E> edgesOf(V vertex);

    boolean removeAllEdges(Collection<? extends E> edges);

    Set<E> removeAllEdges(V sourceVertex, V targetVertex);

    boolean removeAllVertices(Collection<? extends V> vertices);

    E removeEdge(V sourceVertex, V targetVertex);

    boolean removeEdge(E e);

    boolean removeVertex(V v);

    Set<V> vertexSet();

    V getEdgeSource(E e);

    V getEdgeTarget(E e);

    double getEdgeWeight(E e);
}

// End Graph.java
