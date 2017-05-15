package com.abc.basic.algoritms.base.graph.kshortestpath;

import com.abc.basic.algoritms.base.graph.GraphPath;

import java.util.List;

public interface KShortestPathAlgorithm<V, E>
{

    List<GraphPath<V, E>> getPaths(V source, V sink);

}

