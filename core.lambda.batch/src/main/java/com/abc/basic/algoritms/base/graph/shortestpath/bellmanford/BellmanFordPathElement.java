package com.abc.basic.algoritms.base.graph.shortestpath.bellmanford;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.shortestpath.AbstractPathElement;

final class BellmanFordPathElement<V, E>
        extends AbstractPathElement<V, E>
{
    private double cost = 0;
    private double epsilon;

    protected BellmanFordPathElement(
            Graph<V, E> graph, BellmanFordPathElement<V, E> pathElement, E edge, double cost,
            double epsilon)
    {
        super(graph, pathElement, edge);

        this.cost = cost;
        this.epsilon = epsilon;
    }

    /**
     * Copy constructor.
     *
     * @param original source to copy from
     */
    BellmanFordPathElement(BellmanFordPathElement<V, E> original)
    {
        super(original);
        this.cost = original.cost;
        this.epsilon = original.epsilon;
    }

    protected BellmanFordPathElement(V vertex, double epsilon)
    {
        super(vertex);

        this.cost = 0;
        this.epsilon = epsilon;
    }

    public double getCost()
    {
        return this.cost;
    }

    protected boolean improve(
            BellmanFordPathElement<V, E> candidatePrevPathElement, E candidateEdge,
            double candidateCost)
    {
        // to avoid improvement only due to rounding errors.
        if (candidateCost < (getCost() - epsilon)) {
            this.prevPathElement = candidatePrevPathElement;
            this.prevEdge = candidateEdge;
            this.cost = candidateCost;
            this.nHops = candidatePrevPathElement.getHopCount() + 1;

            return true;
        } else {
            return false;
        }
    }
}

