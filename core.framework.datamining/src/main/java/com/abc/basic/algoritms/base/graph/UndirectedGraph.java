package com.abc.basic.algoritms.base.graph;

public interface UndirectedGraph<V, E>
        extends Graph<V, E>
{
    int degreeOf(V vertex);
}
