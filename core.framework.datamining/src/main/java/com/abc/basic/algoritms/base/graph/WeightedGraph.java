package com.abc.basic.algoritms.base.graph;

public interface WeightedGraph<V, E>
        extends Graph<V, E>
{
    double DEFAULT_EDGE_WEIGHT = 1.0;

    void setEdgeWeight(E e, double weight);
}
