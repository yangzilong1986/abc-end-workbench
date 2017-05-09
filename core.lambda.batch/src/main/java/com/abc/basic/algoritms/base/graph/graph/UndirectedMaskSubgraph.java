package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.UndirectedGraph;

import java.util.function.Predicate;

public class UndirectedMaskSubgraph<V, E>
        extends MaskSubgraph<V, E>
        implements UndirectedGraph<V, E>
{


    public UndirectedMaskSubgraph(
            UndirectedGraph<V, E> base, Predicate<V> vertexMask, Predicate<E> edgeMask)
    {
        super(base, vertexMask, edgeMask);
    }

    @Override
    public int degreeOf(V vertex)
    {
        return edgesOf(vertex).size();
    }

}

