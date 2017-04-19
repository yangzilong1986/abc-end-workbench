/*
 * (C) Copyright 2005-2017, by Ewgenij Proschak and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.clique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;

/**
 * Bron-Kerbosch maximal clique enumeration algorithm.
 * 
 * <p>
 * Implementation of the Bron-Kerbosch clique enumeration algorithm as described in:
 * <ul>
 * <li>R. Samudrala and J. Moult. A graph-theoretic algorithm for comparative modeling of protein
 * structure. Journal of Molecular Biology, 279(1):287--302, 1998.</li>
 * </ul>
 * 
 * <p>
 * The algorithm first computes all maximal cliques and then returns the result to the user. A
 * timeout can be set using the constructor parameters.
 *
 * 基础形式是一个递归回溯的搜索算法.通过给定三个集合 (R,P,X).
 *　　初始化集合R,X分别为空,而集合P为所有顶点的集合.
 *　　而每次从集合P中取顶点{v}, 当集合中没有顶点时,两种情况.
 *　　　　1.  集合 R 是最大团, 此时集合X为空.
 *　　　　2.  无最大团,此时回溯.
 *　　对于每一个从集合P中取得得顶点{v},有如下处理:
 *　　　　1. 将顶点{v}加到集合R中, 集合P,X 与 顶点{v}得邻接顶点集合 N{v}相交, 之后递归集合 R,P,X
 *　　　　2. 从集合P中删除顶点{v},并将顶点{v}添加到集合X中.
 *　　　　若 集合 P,X都为空, 则集合R即为最大团.
 *　　　　总的来看就是每次从 集合P中取v后,再在 P∩N{v} 集合中取,一直取相邻,保证集合R中任意顶点间都两两相邻
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @see PivotBronKerboschCliqueFinder
 * @see DegeneracyBronKerboschCliqueFinder
 *
 * @author Ewgenij Proschak
 */
public class BronKerboschCliqueFinder<V, E>
    extends BaseBronKerboschCliqueFinder<V, E>
{
    /**
     * Constructs a new clique finder.
     *
     * @param graph the input graph; must be simple
     */
    public BronKerboschCliqueFinder(Graph<V, E> graph)
    {
        this(graph, 0L, TimeUnit.SECONDS);
    }

    /**
     * Constructs a new clique finder.
     *
     * @param graph the input graph; must be simple
     * @param timeout the maximum time to wait, if zero no timeout
     * @param unit the time unit of the timeout argument
     */
    public BronKerboschCliqueFinder(Graph<V, E> graph, long timeout, TimeUnit unit)
    {
        super(graph, timeout, unit);
    }

    /**
     * Lazily execute the enumeration algorithm.
     */
    @Override
    protected void lazyRun()
    {
        if (allMaximalCliques == null) {
            if (!GraphTests.isSimple(graph)) {
                throw new IllegalArgumentException("Graph must be simple");
            }
            allMaximalCliques = new ArrayList<>();

            long nanosTimeLimit;
            try {
                nanosTimeLimit = Math.addExact(System.nanoTime(), nanos);
            } catch (ArithmeticException ignore) {
                nanosTimeLimit = Long.MAX_VALUE;
            }

            findCliques(
                new ArrayList<>(), new ArrayList<>(graph.vertexSet()), new ArrayList<>(),
                nanosTimeLimit);
        }
    }

    private void findCliques(
        List<V> potentialClique, List<V> candidates, List<V> alreadyFound,
        final long nanosTimeLimit)
    {
        /*
         * Termination condition: check if any already found node is connected to all candidate
         * nodes.
         */
        for (V v : alreadyFound) {
            if (candidates.stream().allMatch(c -> graph.containsEdge(v, c))) {
                return;
            }
        }

        /*
         * Check each candidate
         */
        for (V candidate : new ArrayList<>(candidates)) {
            /*
             * Check if timeout
             */
            if (nanosTimeLimit - System.nanoTime() < 0) {
                timeLimitReached = true;
                return;
            }

            List<V> newCandidates = new ArrayList<>();
            List<V> newAlreadyFound = new ArrayList<>();

            // move candidate node to potentialClique
            potentialClique.add(candidate);
            candidates.remove(candidate);

            // create newCandidates by removing nodes in candidates not
            // connected to candidate node
            for (V newCandidate : candidates) {
                if (graph.containsEdge(candidate, newCandidate)) {
                    newCandidates.add(newCandidate);
                }
            }

            // create newAlreadyFound by removing nodes in alreadyFound
            // not connected to candidate node
            for (V newFound : alreadyFound) {
                if (graph.containsEdge(candidate, newFound)) {
                    newAlreadyFound.add(newFound);
                }
            }

            // if newCandidates and newAlreadyFound are empty
            if (newCandidates.isEmpty() && newAlreadyFound.isEmpty()) {
                // potential clique is maximal clique
                Set<V> maximalClique = new HashSet<>(potentialClique);
                allMaximalCliques.add(maximalClique);
                maxSize = Math.max(maxSize, maximalClique.size());
            } else {
                // recursive call
                findCliques(potentialClique, newCandidates, newAlreadyFound, nanosTimeLimit);
            }

            // move candidate node from potentialClique to alreadyFound
            alreadyFound.add(candidate);
            potentialClique.remove(candidate);
        }
    }

}
