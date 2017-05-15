package com.abc.basic.algoritms.base.graph.util;


import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;

import java.io.Serializable;
import java.util.Set;

public class ArrayUnenforcedSetEdgeSetFactory<V, E>
        implements EdgeSetFactory<V, E>, Serializable
{
    private static final long serialVersionUID = 5936902837403445985L;

    @Override
    public Set<E> createEdgeSet(V vertex)
    {
        return new ArrayUnenforcedSet<>(1);
    }

}
