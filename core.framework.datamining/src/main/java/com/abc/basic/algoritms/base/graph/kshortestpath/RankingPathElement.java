package com.abc.basic.algoritms.base.graph.kshortestpath;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.shortestpath.AbstractPathElement;

final class RankingPathElement<V, E>
        extends AbstractPathElement<V, E>
{

    private double weight;

    RankingPathElement(
            Graph<V, E> graph, RankingPathElement<V, E> pathElement, E edge, double weight)
    {
        super(graph, pathElement, edge);
        this.weight = weight;
    }

    RankingPathElement(V vertex)
    {
        super(vertex);
        this.weight = 0;
    }

    public double getWeight()
    {
        return this.weight;
    }

    @Override
    public RankingPathElement<V, E> getPrevPathElement()
    {
        return (RankingPathElement<V, E>) super.getPrevPathElement();
    }
}