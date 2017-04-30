package org.jgrapht.graph;

import org.jgrapht.*;
import org.jgrapht.graph.builder.*;

/**
 * A directed graph. A default directed graph is a non-simple directed graph in which multiple edges
 * between any two vertices are <i>not</i> permitted, but loops are.
 */
public class DefaultDirectedGraph<V, E>
    extends AbstractBaseGraph<V, E>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 3544953246956466230L;

    public DefaultDirectedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    public DefaultDirectedGraph(EdgeFactory<V, E> ef)
    {
        super(ef, false, true);
    }

    public static <V,
        E> DirectedGraphBuilderBase<V, E, ? extends DefaultDirectedGraph<V, E>, ?> builder(
            Class<? extends E> edgeClass)
    {
        return new DirectedGraphBuilder<>(new DefaultDirectedGraph<>(edgeClass));
    }

    public static <V,
        E> DirectedGraphBuilderBase<V, E, ? extends DefaultDirectedGraph<V, E>, ?> builder(
            EdgeFactory<V, E> ef)
    {
        return new DirectedGraphBuilder<>(new DefaultDirectedGraph<>(ef));
    }
}

// End DefaultDirectedGraph.java
