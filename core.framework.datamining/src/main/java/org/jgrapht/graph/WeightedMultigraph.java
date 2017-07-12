package org.jgrapht.graph;

import org.jgrapht.*;
import org.jgrapht.graph.builder.*;

public class WeightedMultigraph<V, E>
    extends Multigraph<V, E>
    implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3544671793370640696L;

    public WeightedMultigraph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }

    public WeightedMultigraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    public static <V,
        E> UndirectedWeightedGraphBuilderBase<V, E, ? extends WeightedMultigraph<V, E>, ?> builder(
            Class<? extends E> edgeClass)
    {
        return new UndirectedWeightedGraphBuilder<>(new WeightedMultigraph<>(edgeClass));
    }

    public static <V,
        E> UndirectedWeightedGraphBuilderBase<V, E, ? extends WeightedMultigraph<V, E>, ?> builder(
            EdgeFactory<V, E> ef)
    {
        return new UndirectedWeightedGraphBuilder<>(new WeightedMultigraph<>(ef));
    }
}

// End WeightedMultigraph.java
