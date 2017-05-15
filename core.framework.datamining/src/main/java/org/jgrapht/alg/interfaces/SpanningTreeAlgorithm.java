package org.jgrapht.alg.interfaces;

import java.io.*;
import java.util.*;

public interface SpanningTreeAlgorithm<E>
{
    /**
     * Computes a spanning tree.
     *
     * @return a spanning tree
     */
    SpanningTree<E> getSpanningTree();

    /**
     * A spanning tree.
     *
     * @param <E> the graph edge type
     */
    interface SpanningTree<E>
    {
        /**
         * Returns the weight of the spanning tree.
         * 
         * @return weight of the spanning tree
         */
        double getWeight();

        /**
         * Set of edges of the spanning tree.
         * 
         * @return edge set of the spanning tree
         */
        Set<E> getEdges();
    }

    /**
     * Default implementation of the spanning tree interface.
     *
     * @param <E> the graph edge type
     */
    class SpanningTreeImpl<E>
        implements SpanningTree<E>, Serializable
    {
        private static final long serialVersionUID = 402707108331703333L;

        private final double weight;
        private final Set<E> edges;

        /**
         * Construct a new spanning tree.
         *
         * @param edges the edges
         * @param weight the weight
         */
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
