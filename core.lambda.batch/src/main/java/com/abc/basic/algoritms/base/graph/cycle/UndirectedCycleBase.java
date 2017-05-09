package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.UndirectedGraph;

import java.util.List;

public interface UndirectedCycleBase<V, E>
{

    UndirectedGraph<V, E> getGraph();

    void setGraph(UndirectedGraph<V, E> graph);

    List<List<V>> findCycleBase();
}
