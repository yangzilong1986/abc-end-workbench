package com.abc.basic.algoritms.base.graph.edgefactory;

import java.util.Set;

public interface EdgeSetFactory<V, E>
{

    Set<E> createEdgeSet(V vertex);
}

