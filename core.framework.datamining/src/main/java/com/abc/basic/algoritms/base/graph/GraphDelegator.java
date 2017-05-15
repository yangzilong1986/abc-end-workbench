package com.abc.basic.algoritms.base.graph;


import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;

import java.io.Serializable;
import java.util.Set;

public class GraphDelegator<V, E>
        extends AbstractGraph<V, E>
        implements Graph<V, E>, Serializable
{
    private static final long serialVersionUID = 3257005445226181425L;

    private Graph<V, E> delegate;

    public GraphDelegator(Graph<V, E> g)
    {
        super();

        if (g == null) {
            throw new IllegalArgumentException("g must not be null.");
        }

        delegate = g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        return delegate.getAllEdges(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        return delegate.getEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return delegate.getEdgeFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        return delegate.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        return delegate.addEdge(sourceVertex, targetVertex, e);
    }

    @Override
    public boolean addVertex(V v)
    {
        return delegate.addVertex(v);
    }

    @Override
    public boolean containsEdge(E e)
    {
        return delegate.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v)
    {
        return delegate.containsVertex(v);
    }

    public int degreeOf(V vertex)
    {
        return ((UndirectedGraph<V, E>) delegate).degreeOf(vertex);
    }

    @Override
    public Set<E> edgeSet()
    {
        return delegate.edgeSet();
    }


    @Override
    public Set<E> edgesOf(V vertex)
    {
        return delegate.edgesOf(vertex);
    }

    public int inDegreeOf(V vertex)
    {
        return ((DirectedGraph<V, ? extends E>) delegate).inDegreeOf(vertex);
    }

    public Set<E> incomingEdgesOf(V vertex)
    {
        return ((DirectedGraph<V, E>) delegate).incomingEdgesOf(vertex);
    }

    public int outDegreeOf(V vertex)
    {
        return ((DirectedGraph<V, ? extends E>) delegate).outDegreeOf(vertex);
    }

    public Set<E> outgoingEdgesOf(V vertex)
    {
        return ((DirectedGraph<V, E>) delegate).outgoingEdgesOf(vertex);
    }

    @Override
    public boolean removeEdge(E e)
    {
        return delegate.removeEdge(e);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        return delegate.removeEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeVertex(V v)
    {
        return delegate.removeVertex(v);
    }

    @Override
    public String toString()
    {
        return delegate.toString();
    }

    @Override
    public Set<V> vertexSet()
    {
        return delegate.vertexSet();
    }

    @Override
    public V getEdgeSource(E e)
    {
        return delegate.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e)
    {
        return delegate.getEdgeTarget(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        return delegate.getEdgeWeight(e);
    }

    public void setEdgeWeight(E e, double weight)
    {
        ((WeightedGraph<V, E>) delegate).setEdgeWeight(e, weight);
    }
}
