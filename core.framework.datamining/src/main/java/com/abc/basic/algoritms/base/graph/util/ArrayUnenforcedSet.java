package com.abc.basic.algoritms.base.graph.util;

import java.util.*;

public class ArrayUnenforcedSet<E>
        extends ArrayList<E>
        implements Set<E>
{
    private static final long serialVersionUID = -7413250161201811238L;


    public ArrayUnenforcedSet()
    {
        super();
    }

    public ArrayUnenforcedSet(Collection<? extends E> c)
    {
        super(c);
    }

    public ArrayUnenforcedSet(int n)
    {
        super(n);
    }

    @Override
    public boolean equals(Object o)
    {
        return new ArrayUnenforcedSet.SetForEquality().equals(o);
    }

    @Override
    public int hashCode()
    {
        return new ArrayUnenforcedSet.SetForEquality().hashCode();
    }

    private class SetForEquality
            extends AbstractSet<E>
    {
        @Override
        public Iterator<E> iterator()
        {
            return ArrayUnenforcedSet.this.iterator();
        }

        @Override
        public int size()
        {
            return ArrayUnenforcedSet.this.size();
        }
    }
}
