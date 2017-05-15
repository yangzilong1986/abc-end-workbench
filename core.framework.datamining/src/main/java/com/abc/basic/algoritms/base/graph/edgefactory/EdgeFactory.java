package com.abc.basic.algoritms.base.graph.edgefactory;

public interface EdgeFactory<V, E>
{

    E createEdge(V sourceVertex, V targetVertex);
}

