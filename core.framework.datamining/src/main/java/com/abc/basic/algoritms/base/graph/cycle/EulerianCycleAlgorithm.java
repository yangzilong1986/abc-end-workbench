package com.abc.basic.algoritms.base.graph.cycle;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.GraphPath;

public interface EulerianCycleAlgorithm<V, E>
{

    GraphPath<V, E> getEulerianCycle(Graph<V, E> graph);

}
