package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.GraphDelegator;

import java.util.Set;

public class EdgeReversedGraph<V, E>
        extends GraphDelegator<V, E>
        implements DirectedGraph<V, E>
{
    /**
     */
    private static final long serialVersionUID = 9091361782455418631L;

    /**
     * Creates a new EdgeReversedGraph.
     *
     * @param g the base (backing) graph on which the edge-reversed view will be based.
     */
    public EdgeReversedGraph(DirectedGraph<V, E> g)
    {
        super(g);
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        return super.getEdge(targetVertex, sourceVertex);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        return super.getAllEdges(targetVertex, sourceVertex);
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        return super.addEdge(targetVertex, sourceVertex);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        return super.addEdge(targetVertex, sourceVertex, e);
    }

    /**
     * @see DirectedGraph#inDegreeOf(Object)
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        return super.outDegreeOf(vertex);
    }

    /**
     * @see DirectedGraph#outDegreeOf(Object)
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        return super.inDegreeOf(vertex);
    }

    /**
     * @see DirectedGraph#incomingEdgesOf(Object)
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        return super.outgoingEdgesOf(vertex);
    }

    /**
     * @see DirectedGraph#outgoingEdgesOf(Object)
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        return super.incomingEdgesOf(vertex);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        return super.removeEdge(targetVertex, sourceVertex);
    }

    @Override
    public V getEdgeSource(E e)
    {
        return super.getEdgeTarget(e);
    }

    @Override
    public V getEdgeTarget(E e)
    {
        return super.getEdgeSource(e);
    }

    @Override
    public String toString()
    {
        return toStringFromSets(vertexSet(), edgeSet(), true);
    }
}
