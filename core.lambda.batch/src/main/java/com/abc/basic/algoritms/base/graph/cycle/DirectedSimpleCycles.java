package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.DirectedGraph;

import java.util.List;

public interface DirectedSimpleCycles<V, E>
{
    DirectedGraph<V, E> getGraph();

    void setGraph(DirectedGraph<V, E> graph);

    List<List<V>> findSimpleCycles();
}

