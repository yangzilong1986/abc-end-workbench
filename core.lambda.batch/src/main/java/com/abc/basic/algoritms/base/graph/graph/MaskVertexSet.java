package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.util.TypeUtil;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

class MaskVertexSet<V>
        extends AbstractSet<V>
        implements Serializable
{
    private static final long serialVersionUID = 3751931017141472763L;

    private final Set<V> vertexSet;
    private final Predicate<V> mask;

    public MaskVertexSet(Set<V> vertexSet, Predicate<V> mask)
    {
        this.vertexSet = vertexSet;
        this.mask = mask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o)
    {
        if (!vertexSet.contains(o)) {
            return false;
        }
        V v = TypeUtil.uncheckedCast(o, null);
        return !mask.test(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<V> iterator()
    {
        return vertexSet.stream().filter(mask.negate()).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return (int) vertexSet.stream().filter(mask.negate()).count();
    }

}
