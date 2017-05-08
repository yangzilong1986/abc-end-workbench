package com.abc.basic.algoritms.base.graph.shortestpath;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public  abstract class AbstractPathElement<V, E>
{

    protected int nHops;

     protected E prevEdge;

    protected AbstractPathElement<V, E> prevPathElement;

    /**
     * Target vertex.
     */
    private V vertex;


    protected AbstractPathElement(Graph<V, E> graph, AbstractPathElement<V, E> pathElement, E edge)
    {
        this.vertex = Graphs.getOppositeVertex(graph, edge, pathElement.getVertex());
        this.prevEdge = edge;
        this.prevPathElement = pathElement;

        this.nHops = pathElement.getHopCount() + 1;
    }

    protected AbstractPathElement(AbstractPathElement<V, E> original)
    {
        this.nHops = original.nHops;
        this.prevEdge = original.prevEdge;
        this.prevPathElement = original.prevPathElement;
        this.vertex = original.vertex;
    }

    protected AbstractPathElement(V vertex)
    {
        this.vertex = vertex;
        this.prevEdge = null;
        this.prevPathElement = null;

        this.nHops = 0;
    }

    public List<E> createEdgeListPath()
    {
        List<E> path = new ArrayList<>();
        AbstractPathElement<V, E> pathElement = this;

        // while start vertex is not reached.
        while (pathElement.getPrevEdge() != null) {
            path.add(pathElement.getPrevEdge());

            pathElement = pathElement.getPrevPathElement();
        }

        Collections.reverse(path);

        return path;
    }

    public int getHopCount()
    {
        return this.nHops;
    }


    public E getPrevEdge()
    {
        return this.prevEdge;
    }


    public AbstractPathElement<V, E> getPrevPathElement()
    {
        return this.prevPathElement;
    }


    public V getVertex()
    {
        return this.vertex;
    }
}
