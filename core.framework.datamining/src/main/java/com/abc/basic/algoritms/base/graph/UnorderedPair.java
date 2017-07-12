package com.abc.basic.algoritms.base.graph;

import java.io.Serializable;
import java.util.Objects;

public class UnorderedPair<A, B>
        extends Pair<A, B>
        implements Serializable
{
    private static final long serialVersionUID = -3110454174542533876L;


    public UnorderedPair(A a, B b)
    {
        super(a, b);
    }

    @Override
    public String toString()
    {
        return "{" + first + "," + second + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof UnorderedPair))
            return false;

        @SuppressWarnings("unchecked") UnorderedPair<A, B> other = (UnorderedPair<A, B>) o;

        return (Objects.equals(first, other.first) && Objects.equals(second, other.second))
                || (Objects.equals(first, other.second) && Objects.equals(second, other.first));
    }

    @Override
    public int hashCode()
    {
        int hash1 = first == null ? 0 : first.hashCode();
        int hash2 = second == null ? 0 : second.hashCode();
        return hash1 > hash2 ? hash1 * 31 + hash2 : hash2 * 31 + hash1;
    }

    /**
     * Creates new unordered pair of elements pulling of the necessity to provide corresponding
     * types of the elements supplied.
     *
     * @param a first element
     * @param b second element
     * @param <A> the first element type
     * @param <B> the second element type
     * @return new unordered pair
     */
    public static <A, B> UnorderedPair<A, B> of(A a, B b)
    {
        return new UnorderedPair<>(a, b);
    }
}

