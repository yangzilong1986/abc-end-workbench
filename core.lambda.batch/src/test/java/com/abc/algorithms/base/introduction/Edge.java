package com.abc.algorithms.base.introduction;

public abstract class Edge<T> {
    public T source ,target;
    public Edge(T source ,T target) {
        // TODO Auto-generated constructor stub
        this.source=source;
        this.target=target;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }
    /**
     * 有向边和无向边的equal函数不同.有向边要求source→target顺序，无向边不要求
     * */
    @Override
    public abstract boolean equals(Object obj);
}

