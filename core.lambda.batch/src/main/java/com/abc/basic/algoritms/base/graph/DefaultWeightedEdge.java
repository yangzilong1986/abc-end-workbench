package com.abc.basic.algoritms.base.graph;

public class DefaultWeightedEdge
        extends DefaultEdge
{
    private static final long serialVersionUID = 229708706467350994L;

    double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;

    protected double getWeight()
    {
        return weight;
    }
}
