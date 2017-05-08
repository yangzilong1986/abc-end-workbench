package com.abc.basic.algoritms.base.graph.specifics;

import java.util.Set;

public interface Specifics<V, E>
{

    void addVertex(V vertex);

    Set<V> getVertexSet();

    Set<E> getAllEdges(V sourceVertex, V targetVertex);

    E getEdge(V sourceVertex, V targetVertex);

    void addEdgeToTouchingVertices(E e);

    int degreeOf(V vertex);

    Set<E> edgesOf(V vertex);

    int inDegreeOf(V vertex);

    Set<E> incomingEdgesOf(V vertex);

    int outDegreeOf(V vertex);

    Set<E> outgoingEdgesOf(V vertex);

    void removeEdgeFromTouchingVertices(E e);
}

