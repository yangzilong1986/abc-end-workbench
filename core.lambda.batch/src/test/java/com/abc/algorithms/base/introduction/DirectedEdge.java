package com.abc.algorithms.base.introduction;

public class DirectedEdge<T> extends Edge<T>{

    public DirectedEdge(T source, T target) {
        super(source, target);
        // TODO Auto-generated constructor stub
    }

    /**
     *有向边，source和target必须对应相等
     * */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge<?> other = (Edge<?>) obj;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }
}

