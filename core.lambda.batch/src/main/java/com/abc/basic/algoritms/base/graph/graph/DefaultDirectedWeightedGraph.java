package com.abc.basic.algoritms.base.graph.graph;


import com.abc.basic.algoritms.base.graph.DefaultDirectedGraph;
import com.abc.basic.algoritms.base.graph.WeightedGraph;
import com.abc.basic.algoritms.base.graph.edgefactory.ClassBasedEdgeFactory;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

public class DefaultDirectedWeightedGraph<V, E>
        extends DefaultDirectedGraph<V, E>
        implements WeightedGraph<V, E>
{
    private static final long serialVersionUID = 3761405317841171513L;

    public DefaultDirectedWeightedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }


    public DefaultDirectedWeightedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
    }


}
