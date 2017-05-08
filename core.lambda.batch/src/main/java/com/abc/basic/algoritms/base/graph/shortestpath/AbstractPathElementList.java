package com.abc.basic.algoritms.base.graph.shortestpath;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;

import java.util.AbstractList;
import java.util.ArrayList;

public abstract class AbstractPathElementList<V, E, T extends AbstractPathElement<V, E>>
        extends AbstractList<T>
{
    protected Graph<V, E> graph;

    protected int maxSize;

    protected ArrayList<T> pathElements = new ArrayList<>();

    /**
     * Target vertex of the paths.
     */
    protected V vertex;


    protected AbstractPathElementList(
            Graph<V, E> graph, int maxSize, AbstractPathElementList<V, E, T> elementList, E edge)
    {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize is negative or 0");
        }
        if (elementList == null) {
            throw new NullPointerException("elementList is null");
        }
        if (edge == null) {
            throw new NullPointerException("edge is null");
        }

        this.graph = graph;
        this.maxSize = maxSize;
        this.vertex = Graphs.getOppositeVertex(graph, edge, elementList.getVertex());
    }


    protected AbstractPathElementList(Graph<V, E> graph, int maxSize, T pathElement)
    {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize is negative or 0");
        }
        if (pathElement == null) {
            throw new NullPointerException("pathElement is null");
        }
        if (pathElement.getPrevEdge() != null) {
            throw new IllegalArgumentException("path must be empty");
        }

        this.graph = graph;
        this.maxSize = maxSize;
        this.vertex = pathElement.getVertex();

        this.pathElements.add(pathElement);
    }

    protected AbstractPathElementList(Graph<V, E> graph, int maxSize, V vertex)
    {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize is negative or 0");
        }

        this.graph = graph;
        this.maxSize = maxSize;
        this.vertex = vertex;
    }

    @Override
    public T get(int index)
    {
        return this.pathElements.get(index);
    }

    public V getVertex()
    {
        return this.vertex;
    }

    public int size()
    {
        return this.pathElements.size();
    }
}


