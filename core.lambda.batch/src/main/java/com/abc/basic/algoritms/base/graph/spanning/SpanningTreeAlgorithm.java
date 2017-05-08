package com.abc.basic.algoritms.base.graph.spanning;

import java.io.Serializable;
import java.util.Set;

public interface SpanningTreeAlgorithm<E>
{

    SpanningTree<E> getSpanningTree();

    interface SpanningTree<E>
    {

        double getWeight();

        Set<E> getEdges();
    }

    class SpanningTreeImpl<E>
            implements SpanningTree<E>, Serializable
    {
        private static final long serialVersionUID = 402707108331703333L;

        private final double weight;
        private final Set<E> edges;

        public SpanningTreeImpl(Set<E> edges, double weight)
        {
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public double getWeight()
        {
            return weight;
        }

        @Override
        public Set<E> getEdges()
        {
            return edges;
        }

        @Override
        public String toString()
        {
            return "Spanning-Tree [weight=" + weight + ", edges=" + edges + "]";
        }
    }

}
