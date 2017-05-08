package com.abc.basic.algoritms.base.graph.traverse;


import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractGraphIterator<V, E>
        implements GraphIterator<V, E>
{
    private boolean crossComponentTraversal = true;
    private boolean reuseEvents = false;

    protected Specifics<V, E> specifics;


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


    /**
     * {@inheritDoc}
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

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
        @Override
        public Set<EE> edgesOf(VV vertex)
        {
            return graph.edgesOf(vertex);
        }
    }
}

