package org.jgrapht.graph;

import org.jgrapht.*;
import org.jgrapht.graph.builder.*;

public class DefaultDirectedWeightedGraph<V, E>
    extends DefaultDirectedGraph<V, E>
    implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3761405317841171513L;

    /**
     * Creates a new directed weighted graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public DefaultDirectedWeightedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    /**
     * Creates a new directed weighted graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public DefaultDirectedWeightedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param edgeClass class on which to base factory for edges
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V, E> DirectedWeightedGraphBuilderBase<V, E,
        ? extends DefaultDirectedWeightedGraph<V, E>, ?> builder(Class<? extends E> edgeClass)
    {
        return new DirectedWeightedGraphBuilder<>(new DefaultDirectedWeightedGraph<>(edgeClass));
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param ef the edge factory of the new graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V, E> DirectedWeightedGraphBuilderBase<V, E,
        ? extends DefaultDirectedWeightedGraph<V, E>, ?> builder(EdgeFactory<V, E> ef)
    {
        return new DirectedWeightedGraphBuilder<>(new DefaultDirectedWeightedGraph<>(ef));
    }
}

// End DefaultDirectedWeightedGraph.java
