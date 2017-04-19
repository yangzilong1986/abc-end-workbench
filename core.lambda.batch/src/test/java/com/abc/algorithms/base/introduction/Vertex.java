package com.abc.algorithms.base.introduction;

import java.util.LinkedHashSet;
import java.util.Set;
public class Vertex<T> {
    public Color color;
    public int distance;
    public Vertex<T> parent;
    public T value;
    public int discover,finish;//深度优先搜索的第一次发现和结束的时间
    public Set<Vertex<T>> adjacencyVertices;// 邻接表,采用集合来表示

    public Vertex(T t) {
        // TODO Auto-generated constructor stub
        color = Color.WHITE;
        distance = Integer.MAX_VALUE;
        parent = null;
        value = t;
        adjacencyVertices = new LinkedHashSet<Vertex<T>>();
        discover=finish=0;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex<?> other = (Vertex<?>) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder builder=new StringBuilder();
        builder.append(value);
        return builder.toString();
    }

}

