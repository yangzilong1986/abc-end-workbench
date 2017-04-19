package com.abc.algorithms.base.introduction;

public class UndirectedEdge<T> extends Edge<T> {

    public UndirectedEdge(T source, T target) {
        super(source, target);
        // TODO Auto-generated constructor stub
    }
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge<?> other = (Edge<?>) obj;

        if (source == null) {
            if (other.source != null&&other.target!=null)
                return false;
        } else if (!source.equals(other.source)&&!source.equals(other.target))
            return false;
        if (target == null) {
            if (other.target != null&&other.source!=null)
                return false;
        } else if (!target.equals(other.target)&&!target.equals(other.source))
            return false;
        return true;
    }

}

