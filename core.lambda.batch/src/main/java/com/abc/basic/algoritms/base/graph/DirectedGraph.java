package com.abc.basic.algoritms.base.graph;

import java.util.Set;

public interface DirectedGraph<V, E>
        extends Graph<V, E>
{

    int inDegreeOf(V vertex);

    Set<E> incomingEdgesOf(V vertex);

    int outDegreeOf(V vertex);

    Set<E> outgoingEdgesOf(V vertex);
}

