package org.jgrapht.graph;

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.util.*;

public class AsUndirectedGraph<V, E>
    extends GraphDelegator<V, E>
    implements Serializable, UndirectedGraph<V, E>
{
    private static final long serialVersionUID = 3257845485078065462L; // @todo renew
    private static final String NO_EDGE_ADD = "this graph does not support edge addition";
    private static final String UNDIRECTED = "this graph only supports undirected operations";

    public AsUndirectedGraph(DirectedGraph<V, E> g)
    {
        super(g);
    }

    /**
     * @see Graph#getAllEdges(Object, Object)
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> forwardList = super.getAllEdges(sourceVertex, targetVertex);

        if (sourceVertex.equals(targetVertex)) {
            // avoid duplicating loops
            return forwardList;
        }

        Set<E> reverseList = super.getAllEdges(targetVertex, sourceVertex);
        Set<E> list = new ArrayUnenforcedSet<>(forwardList.size() + reverseList.size());
        list.addAll(forwardList);
        list.addAll(reverseList);

        return list;
    }

    /**
     * @see Graph#getEdge(Object, Object)
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        E edge = super.getEdge(sourceVertex, targetVertex);

        if (edge != null) {
            return edge;
        }

        // try the other direction
        return super.getEdge(targetVertex, sourceVertex);
    }

    /**
     * @see Graph#addEdge(Object, Object)
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    /**
     * @see Graph#addEdge(Object, Object, Object)
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    /**
     * @see UndirectedGraph#degreeOf(Object)
     */
    @Override
    public int degreeOf(V vertex)
    {
        // this counts loops twice, which is consistent with AbstractBaseGraph
        return super.inDegreeOf(vertex) + super.outDegreeOf(vertex);
    }

    /**
     * @see DirectedGraph#inDegreeOf(Object)
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }

    /**
     * @see DirectedGraph#incomingEdgesOf(Object)
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }

    /**
     * @see DirectedGraph#outDegreeOf(Object)
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }

    /**
     * @see DirectedGraph#outgoingEdgesOf(Object)
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }

    /**
     * @see AbstractBaseGraph#toString()
     */
    @Override
    public String toString()
    {
        return super.toStringFromSets(vertexSet(), edgeSet(), false);
    }
}

// End AsUndirectedGraph.java
