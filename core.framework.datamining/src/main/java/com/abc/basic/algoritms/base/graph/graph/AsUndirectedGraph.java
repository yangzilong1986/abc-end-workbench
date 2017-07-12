package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.GraphDelegator;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSet;

import java.io.Serializable;
import java.util.Set;

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

    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new UnsupportedOperationException(NO_EDGE_ADD);
    }

    @Override
    public int degreeOf(V vertex)
    {
        // this counts loops twice, which is consistent with AbstractBaseGraph
        return super.inDegreeOf(vertex) + super.outDegreeOf(vertex);
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }


    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(UNDIRECTED);
    }

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

    @Override
    public String toString()
    {
        return super.toStringFromSets(vertexSet(), edgeSet(), false);
    }
}
