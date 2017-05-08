package com.abc.basic.algoritms.base.graph.shortestpath;

import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.GraphPath;

public interface ShortestPathAlgorithm<V, E>
{

    GraphPath<V, E> getPath(V source, V sink);

    double getPathWeight(V source, V sink);

    SingleSourcePaths<V, E> getPaths(V source);

    interface SingleSourcePaths<V, E>
    {

        Graph<V, E> getGraph();

        V getSourceVertex();

        double getWeight(V sink);

        GraphPath<V, E> getPath(V sink);
    }

}

