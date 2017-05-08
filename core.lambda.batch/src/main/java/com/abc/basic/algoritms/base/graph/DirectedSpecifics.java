package com.abc.basic.algoritms.base.graph;


import com.abc.basic.algoritms.base.graph.edgefactory.EdgeSetFactory;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSet;
import com.abc.basic.algoritms.base.graph.util.ArrayUnenforcedSetEdgeSetFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DirectedSpecifics<V, E>
        implements Specifics<V, E>, Serializable
{
    private static final long serialVersionUID = 8971725103718958232L;
    private static final String NOT_IN_DIRECTED_GRAPH = "no such operation in a directed graph";

    protected AbstractBaseGraph<V, E> abstractBaseGraph;
    protected Map<V,DirectedEdgeContainer<V, E>> vertexMapDirected;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    public DirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>(),
                new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public DirectedSpecifics(
            AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap)
    {
        this(abstractBaseGraph, vertexMap,
                new ArrayUnenforcedSetEdgeSetFactory<>());
    }

    public DirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph,
                             Map<V,DirectedEdgeContainer<V, E>> vertexMap,
            EdgeSetFactory<V, E> edgeSetFactory)
    {
        this.abstractBaseGraph = abstractBaseGraph;
        this.vertexMapDirected = vertexMap;
        this.edgeSetFactory = edgeSetFactory;
    }

    @Override
    public void addVertex(V v)
    {
        // add with a lazy edge container entry
        vertexMapDirected.put(v, null);
    }

    @Override
    public Set<V> getVertexSet()
    {
        return vertexMapDirected.keySet();
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> edges = null;

        if (abstractBaseGraph.containsVertex(sourceVertex)
                && abstractBaseGraph.containsVertex(targetVertex))
        {
            edges = new ArrayUnenforcedSet<>();

            DirectedEdgeContainer<V, E> ec = getEdgeContainer(sourceVertex);

            for (E e : ec.outgoing) {
                if (abstractBaseGraph.getEdgeTarget(e).equals(targetVertex)) {
                    edges.add(e);
                }
            }
        }

        return edges;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        if (abstractBaseGraph.containsVertex(sourceVertex)
                && abstractBaseGraph.containsVertex(targetVertex))
        {
            DirectedEdgeContainer<V, E> ec = getEdgeContainer(sourceVertex);

            for (E e : ec.outgoing) {
                if (abstractBaseGraph.getEdgeTarget(e).equals(targetVertex)) {
                    return e;
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEdgeToTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).addOutgoingEdge(e);
        getEdgeContainer(target).addIncomingEdge(e);
    }

    @Override
    public int degreeOf(V vertex)
    {

        throw new UnsupportedOperationException(NOT_IN_DIRECTED_GRAPH);
    }

    @Override
    /**
     * 获取顶点的边
     */
    public Set<E> edgesOf(V vertex)
    {
        ArrayUnenforcedSet<E> inAndOut =
                new ArrayUnenforcedSet<>(getEdgeContainer(vertex).incoming);
        inAndOut.addAll(getEdgeContainer(vertex).outgoing);

        // we have two copies for each self-loop - remove one of them.
        if (abstractBaseGraph.isAllowingLoops()) {
            Set<E> loops = getAllEdges(vertex, vertex);

            for (int i = 0; i < inAndOut.size();) {
                E e = inAndOut.get(i);

                if (loops.contains(e)) {
                    inAndOut.remove(i);
                    loops.remove(e); // so we remove it only once
                } else {
                    i++;
                }
            }
        }
        return Collections.unmodifiableSet(inAndOut);
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        return getEdgeContainer(vertex).incoming.size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableIncomingEdges();
    }

    @Override
    public int outDegreeOf(V vertex)
    {
        return getEdgeContainer(vertex).outgoing.size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableOutgoingEdges();
    }

    @Override
    public void removeEdgeFromTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeOutgoingEdge(e);
        getEdgeContainer(target).removeIncomingEdge(e);
    }


    protected DirectedEdgeContainer<V, E> getEdgeContainer(V vertex)
    {

        DirectedEdgeContainer<V, E> ec = vertexMapDirected.get(vertex);

        if (ec == null) {
            ec = new DirectedEdgeContainer<>(edgeSetFactory, vertex);
            vertexMapDirected.put(vertex, ec);
        }

        return ec;
    }
}

