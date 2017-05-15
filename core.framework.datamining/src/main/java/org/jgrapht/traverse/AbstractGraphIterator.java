package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.event.*;

public abstract class AbstractGraphIterator<V, E>
    implements GraphIterator<V, E>
{
    private List<TraversalListener<V, E>> traversalListeners = new ArrayList<>();
    private boolean crossComponentTraversal = true;
    private boolean reuseEvents = false;

    // We keep this cached redundantly with traversalListeners.size()
    // so that subclasses can use it as a fast check to see if
    // event firing calls can be skipped.
    protected int nListeners = 0;
    // TODO: support ConcurrentModificationException if graph modified
    // during iteration.
    protected FlyweightEdgeEvent<V, E> reusableEdgeEvent;
    protected FlyweightVertexEvent<V> reusableVertexEvent;
    protected Specifics<V, E> specifics;

    /**
     * Sets the cross component traversal flag - indicates whether to traverse the graph across
     * connected components.
     *
     * @param crossComponentTraversal if <code>true</code> traverses across connected components.
     */
    public void setCrossComponentTraversal(boolean crossComponentTraversal)
    {
        this.crossComponentTraversal = crossComponentTraversal;
    }

    @Override
    public boolean isCrossComponentTraversal()
    {
        return crossComponentTraversal;
    }

    @Override
    public void setReuseEvents(boolean reuseEvents)
    {
        this.reuseEvents = reuseEvents;
    }


    @Override
    public boolean isReuseEvents()
    {
        return reuseEvents;
    }

    @Override
    public void addTraversalListener(TraversalListener<V, E> l)
    {
        if (!traversalListeners.contains(l)) {
            traversalListeners.add(l);
            nListeners = traversalListeners.size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeTraversalListener(TraversalListener<V, E> l)
    {
        traversalListeners.remove(l);
        nListeners = traversalListeners.size();
    }

    protected void fireConnectedComponentFinished(ConnectedComponentTraversalEvent e)
    {
        for (int i = 0; i < nListeners; i++) {
            TraversalListener<V, E> l = traversalListeners.get(i);
            l.connectedComponentFinished(e);
        }
    }

    protected void fireConnectedComponentStarted(ConnectedComponentTraversalEvent e)
    {
        for (int i = 0; i < nListeners; i++) {
            TraversalListener<V, E> l = traversalListeners.get(i);
            l.connectedComponentStarted(e);
        }
    }

    protected void fireEdgeTraversed(EdgeTraversalEvent<E> e)
    {
        for (int i = 0; i < nListeners; i++) {
            TraversalListener<V, E> l = traversalListeners.get(i);
            l.edgeTraversed(e);
        }
    }

    protected void fireVertexTraversed(VertexTraversalEvent<V> e)
    {
        for (int i = 0; i < nListeners; i++) {
            TraversalListener<V, E> l = traversalListeners.get(i);
            l.vertexTraversed(e);
        }
    }

    protected void fireVertexFinished(VertexTraversalEvent<V> e)
    {
        for (int i = 0; i < nListeners; i++) {
            TraversalListener<V, E> l = traversalListeners.get(i);
            l.vertexFinished(e);
        }
    }

    static class FlyweightEdgeEvent<VV, localE>
        extends EdgeTraversalEvent<localE>
    {
        private static final long serialVersionUID = 4051327833765000755L;

        public FlyweightEdgeEvent(Object eventSource, localE edge)
        {
            super(eventSource, edge);
        }

        protected void setEdge(localE edge)
        {
            this.edge = edge;
        }
    }

    /**
     * A reusable vertex event.
     *
     * @author Barak Naveh
     * @since Aug 11, 2003
     */
    static class FlyweightVertexEvent<VV>
        extends VertexTraversalEvent<VV>
    {
        private static final long serialVersionUID = 3834024753848399924L;

        public FlyweightVertexEvent(Object eventSource, VV vertex)
        {
            super(eventSource, vertex);
        }

        protected void setVertex(VV vertex)
        {
            this.vertex = vertex;
        }
    }

    // -------------------------------------------------------------------------
    static <V, E> Specifics<V, E> createGraphSpecifics(Graph<V, E> g)
    {
        if (g instanceof DirectedGraph<?, ?>) {
            return new DirectedSpecifics<>((DirectedGraph<V, E>) g);
        } else {
            return new UndirectedSpecifics<>(g);
        }
    }

    abstract static class Specifics<VV, EE>
    {
        public abstract Set<? extends EE> edgesOf(VV vertex);
    }

    /**
     * An implementation of {@link Specifics} for a directed graph.
     */
    static class DirectedSpecifics<VV, EE>
        extends Specifics<VV, EE>
    {
        private DirectedGraph<VV, EE> graph;
        public DirectedSpecifics(DirectedGraph<VV, EE> g)
        {
            graph = g;
        }

        @Override
        public Set<? extends EE> edgesOf(VV vertex)
        {
            return graph.outgoingEdgesOf(vertex);
        }
    }

    static class UndirectedSpecifics<VV, EE>
        extends Specifics<VV, EE>
    {
        private Graph<VV, EE> graph;

        public UndirectedSpecifics(Graph<VV, EE> g)
        {
            graph = g;
        }

        /**
         * @see CrossComponentIterator.Specifics#edgesOf(Object)
         */
        @Override
        public Set<EE> edgesOf(VV vertex)
        {
            return graph.edgesOf(vertex);
        }
    }
}

// End AbstractGraphIterator.java
