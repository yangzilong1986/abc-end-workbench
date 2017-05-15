package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;

public class GraphWalk<V, E>
    implements GraphPath<V, E>
{
    protected Graph<V, E> graph;

    protected List<V> vertexList;
    protected List<E> edgeList;

    protected V startVertex;

    protected V endVertex;

    protected double weight;

    public GraphWalk(Graph<V, E> graph, V startVertex, V endVertex, List<E> edgeList, double weight)
    {
        this(graph, startVertex, endVertex, null, edgeList, weight);
    }

    public GraphWalk(Graph<V, E> graph, List<V> vertexList, double weight)
    {
        this(
            graph, (vertexList.isEmpty() ? null : vertexList.get(0)),
            (vertexList.isEmpty() ? null : vertexList.get(vertexList.size() - 1)), vertexList, null,
            weight);
    }

    public GraphWalk(
        Graph<V, E> graph, V startVertex, V endVertex, List<V> vertexList, List<E> edgeList,
        double weight)
    {
        if (vertexList == null && edgeList == null)
            throw new IllegalArgumentException("Vertex list and edge list cannot both be null!");

        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.vertexList = vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public V getStartVertex()
    {
        return startVertex;
    }

    @Override
    public V getEndVertex()
    {
        return endVertex;
    }

    @Override
    public List<E> getEdgeList()
    {
        return (edgeList != null ? edgeList : GraphPath.super.getEdgeList());
    }

    @Override
    public List<V> getVertexList()
    {
        return (vertexList != null ? vertexList : GraphPath.super.getVertexList());
    }

    @Override
    public double getWeight()
    {
        return weight;
    }

    @Override
    public int getLength()
    {
        if (edgeList != null)
            return edgeList.size();
        else if (vertexList != null && !vertexList.isEmpty())
            return vertexList.size() - 1;
        else
            return 0;
    }

    @Override
    public String toString()
    {
        if (vertexList != null)
            return vertexList.toString();
        else
            return edgeList.toString();
    }
}

// End GraphPathImpl.java
