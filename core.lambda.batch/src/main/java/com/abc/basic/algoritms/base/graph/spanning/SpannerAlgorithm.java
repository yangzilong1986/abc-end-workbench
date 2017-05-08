package com.abc.basic.algoritms.base.graph.spanning;

import java.io.Serializable;
import java.util.Set;

public interface SpannerAlgorithm<E>
{

    Spanner<E> getSpanner();

    interface Spanner<E>
    {

        double getWeight();

        Set<E> getEdges();
    }

    /**
     * Default implementation of the spanner interface.
     *
     * @param <E> the graph edge type
     */
    class SpannerImpl<E>
            implements Spanner<E>, Serializable
    {
        private static final long serialVersionUID = 5951646499902668516L;

        private final double weight;
        private final Set<E> edges;

        /**
         * Construct a new spanner
         *
         * @param edges the edges
         * @param weight the weight
         */
        public SpannerImpl(Set<E> edges, double weight)
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
            return "Spanner [weight=" + weight + ", edges=" + edges + "]";
        }
    }

}

