package com.abc.basic.algoritms.base.graph.strongconnectivity;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.graph.DirectedSubgraph;

import java.util.List;
import java.util.Set;

/**
 * An interface to the StrongConnectivityInspector algorithm classes. These classes verify whether
 * the graph is strongly connected.
 * 有向图的强连通性
 * 无向图和有向图的定义如下：
 *  自反省，任意顶点和自己都是强连通的
 *  对称性，如果v和w是强连通的，那么w和v也是强连通的
 *  传递性，如果v和w是强连通的，并且w和x也是强连通的，那么v和x也是强连通的
 *
 */
public interface StrongConnectivityAlgorithm<V, E>
{

    DirectedGraph<V, E> getGraph();

    boolean isStronglyConnected();

    List<Set<V>> stronglyConnectedSets();


    List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs();
}
