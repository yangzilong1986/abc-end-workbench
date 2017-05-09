package com.abc.basic.algoritms.base.graph.kshortestpath;

import com.abc.basic.algoritms.base.graph.DirectedGraph;
import com.abc.basic.algoritms.base.graph.Graph;
import com.abc.basic.algoritms.base.graph.Graphs;
import com.abc.basic.algoritms.base.graph.UndirectedGraph;
import com.abc.basic.algoritms.base.graph.events.ConnectivityInspector;
import com.abc.basic.algoritms.base.graph.graph.DirectedMaskSubgraph;
import com.abc.basic.algoritms.base.graph.graph.UndirectedMaskSubgraph;
import com.abc.basic.algoritms.base.graph.shortestpath.AbstractPathElementList;


import java.util.*;

final class RankingPathElementList<V, E>
        extends AbstractPathElementList<V, E, RankingPathElement<V, E>>
{

    private V guardVertexToNotDisconnect = null;

    private Map<RankingPathElement<V, E>, Boolean> path2disconnect =
            new HashMap<RankingPathElement<V, E>, Boolean>();


    private PathValidator<V, E> externalPathValidator = null;


    RankingPathElementList(Graph<V, E> graph, int maxSize, RankingPathElement<V, E> pathElement)
    {
        this(graph, maxSize, pathElement, null);
    }

    RankingPathElementList(
            Graph<V, E> graph, int maxSize, RankingPathElement<V, E> pathElement,
            PathValidator<V, E> pathValidator)
    {
        super(graph, maxSize, pathElement);
        this.externalPathValidator = pathValidator;
    }

    RankingPathElementList(
            Graph<V, E> graph, int maxSize, RankingPathElementList<V, E> elementList, E edge)
    {
        this(graph, maxSize, elementList, edge, null);

        assert (!this.pathElements.isEmpty());
    }


    RankingPathElementList(
            Graph<V, E> graph, int maxSize, RankingPathElementList<V, E> elementList, E edge,
            V guardVertexToNotDisconnect)
    {
        this(graph, maxSize, elementList, edge, guardVertexToNotDisconnect, null);
    }

   RankingPathElementList(
            Graph<V, E> graph, int maxSize, RankingPathElementList<V, E> elementList, E edge,
            V guardVertexToNotDisconnect, PathValidator<V, E> pathValidator)
    {
        super(graph, maxSize, elementList, edge);
        this.guardVertexToNotDisconnect = guardVertexToNotDisconnect;
        this.externalPathValidator = pathValidator;

        // loop over the path elements in increasing order of weight.
        for (int i = 0; (i < elementList.size()) && (size() < maxSize); i++) {
            RankingPathElement<V, E> prevPathElement = elementList.get(i);

            if (isNotValidPath(prevPathElement, edge)) {
                // go to the next path element in the loop
                continue;
            }

            double weight = calculatePathWeight(prevPathElement, edge);
            RankingPathElement<V, E> newPathElement =
                    new RankingPathElement<>(this.graph, prevPathElement, edge, weight);

            // the new path is inserted at the end of the list.
            this.pathElements.add(newPathElement);
        }
    }

    RankingPathElementList(Graph<V, E> graph, int maxSize, V vertex)
    {
        this(graph, maxSize, vertex, null);
    }

    RankingPathElementList(
            Graph<V, E> graph, int maxSize, V vertex, PathValidator<V, E> pathValidator)
    {
        super(graph, maxSize, vertex);
        this.externalPathValidator = pathValidator;
    }


    public boolean addPathElements(RankingPathElementList<V, E> elementList, E edge)
    {
        assert (this.vertex
                .equals(Graphs.getOppositeVertex(this.graph, edge, elementList.getVertex())));

        boolean pathAdded = false;

        // loop over the paths elements of the list at vertex v.
        for (int vIndex = 0, yIndex = 0; vIndex < elementList.size(); vIndex++) {
            RankingPathElement<V, E> prevPathElement = elementList.get(vIndex);

            if (isNotValidPath(prevPathElement, edge)) {
                // checks if path is simple and if guard-vertex is not
                // disconnected.
                continue;
            }
            double newPathWeight = calculatePathWeight(prevPathElement, edge);
            RankingPathElement<V, E> newPathElement =
                    new RankingPathElement<>(this.graph, prevPathElement, edge, newPathWeight);

            // loop over the paths of the list at vertex y from yIndex to the
            // end.
            RankingPathElement<V, E> yPathElement = null;
            for (; yIndex < size(); yIndex++) {
                yPathElement = get(yIndex);

                // case when the new path is shorter than the path Py stored at
                // index y
                if (newPathWeight < yPathElement.getWeight()) {
                    this.pathElements.add(yIndex, newPathElement);
                    pathAdded = true;

                    // ensures max size limit is not exceeded.
                    if (size() > this.maxSize) {
                        this.pathElements.remove(this.maxSize);
                    }
                    break;
                }

                // case when the new path is of the same length as the path Py
                // stored at index y
                if (newPathWeight == yPathElement.getWeight()) {
                    this.pathElements.add(yIndex + 1, newPathElement);
                    pathAdded = true;

                    // ensures max size limit is not exceeded.
                    if (size() > this.maxSize) {
                        this.pathElements.remove(this.maxSize);
                    }
                    break;
                }
            }

            // case when the new path is longer than the longest path in the
            // list (Py stored at the last index y)
            if (newPathWeight > yPathElement.getWeight()) {
                // ensures max size limit is not exceeded.
                if (size() < this.maxSize) {
                    // the new path is inserted at the end of the list.
                    this.pathElements.add(newPathElement);
                    pathAdded = true;
                } else {
                    // max size limit is reached -> end of the loop over the
                    // paths elements of the list at vertex v.
                    break;
                }
            }
        }

        return pathAdded;
    }

    List<RankingPathElement<V, E>> getPathElements()
    {
        return this.pathElements;
    }

    private double calculatePathWeight(RankingPathElement<V, E> pathElement, E edge)
    {
        double pathWeight = this.graph.getEdgeWeight(edge);

        // otherwise it's the start vertex.
        if ((pathElement.getPrevEdge() != null)) {
            pathWeight += pathElement.getWeight();
        }

        return pathWeight;
    }

    private boolean isGuardVertexDisconnected(RankingPathElement<V, E> prevPathElement)
    {
        if (this.guardVertexToNotDisconnect == null) {
            return false;
        }

        if (this.path2disconnect.containsKey(prevPathElement)) {
            return this.path2disconnect.get(prevPathElement);
        }

        ConnectivityInspector<V, E> connectivityInspector;
        PathMask<V, E> connectivityMask = new PathMask<>(prevPathElement);

        if (this.graph instanceof DirectedGraph<?, ?>) {
            DirectedMaskSubgraph<V,
                                E> connectivityGraph = new DirectedMaskSubgraph<>(
                    (DirectedGraph<V, E>) this.graph, v -> connectivityMask.isVertexMasked(v),
                    e -> connectivityMask.isEdgeMasked(e));
            connectivityInspector = new ConnectivityInspector<>(connectivityGraph);
        } else {
            UndirectedMaskSubgraph<V,
                                E> connectivityGraph = new UndirectedMaskSubgraph<>(
                    (UndirectedGraph<V, E>) this.graph, v -> connectivityMask.isVertexMasked(v),
                    e -> connectivityMask.isEdgeMasked(e));
            connectivityInspector = new ConnectivityInspector<>(connectivityGraph);
        }

        if (connectivityMask.isVertexMasked(this.guardVertexToNotDisconnect)) {
            // the guard-vertex was already in the path element -> invalid path
            this.path2disconnect.put(prevPathElement, true);
            return true;
        }

        if (!connectivityInspector.pathExists(this.vertex, this.guardVertexToNotDisconnect)) {
            this.path2disconnect.put(prevPathElement, true);
            return true;
        }

        this.path2disconnect.put(prevPathElement, false);
        return false;
    }

    private boolean isNotValidPath(RankingPathElement<V, E> prevPathElement, E edge)
    {
        if (!isSimplePath(prevPathElement, edge)) {
            return true;
        }
        if (isGuardVertexDisconnected(prevPathElement)) {
            return true;
        }
        if (externalPathValidator != null
                && !externalPathValidator.isValidPath(prevPathElement, edge))
        {
            return true;

        } else {
            return false;
        }
    }

    private boolean isSimplePath(RankingPathElement<V, E> prevPathElement, E edge)
    {
        V endVertex = Graphs.getOppositeVertex(this.graph, edge, prevPathElement.getVertex());
        assert (endVertex.equals(this.vertex));

        RankingPathElement<V, E> pathElementToTest = prevPathElement;
        do {
            if (pathElementToTest.getVertex().equals(endVertex)) {
                return false;
            } else {
                pathElementToTest = pathElementToTest.getPrevPathElement();
            }
        } while (pathElementToTest != null);

        return true;
    }

    private static class PathMask<V, E>
    {
        private Set<E> maskedEdges;

        private Set<V> maskedVertices;

        PathMask(RankingPathElement<V, E> pathElement)
        {
            this.maskedEdges = new HashSet<>();
            this.maskedVertices = new HashSet<>();

            while (pathElement.getPrevEdge() != null) {
                this.maskedEdges.add(pathElement.getPrevEdge());
                this.maskedVertices.add(pathElement.getVertex());
                pathElement = pathElement.getPrevPathElement();
            }
            this.maskedVertices.add(pathElement.getVertex());
        }

        public boolean isEdgeMasked(E edge)
        {
            return this.maskedEdges.contains(edge);
        }

        public boolean isVertexMasked(V vertex)
        {
            return this.maskedVertices.contains(vertex);
        }
    }
}
