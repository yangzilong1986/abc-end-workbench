package com.abc.basic.algoritms.base.graph.graph;

import com.abc.basic.algoritms.base.graph.SimpleDirectedGraph;
import com.abc.basic.algoritms.base.graph.edgefactory.EdgeFactory;
import com.abc.basic.algoritms.base.graph.traverse.AbstractGraphIterator;
import com.abc.basic.algoritms.base.graph.traverse.DepthFirstIterator;

import java.io.Serializable;
import java.util.*;

public class DirectedAcyclicGraph<V, E>
        extends SimpleDirectedGraph<V, E>
        implements Iterable<V>
{
    private static final long serialVersionUID = 4522128427004938150L;

    private TopoComparator<V> topoComparator;

    private TopoOrderMapping<V> topoOrderMap;

    private int maxTopoIndex = 0;
    private int minTopoIndex = 0;

    // this update count is used to keep internal topological iterators honest
    private long topologyUpdateCount = 0;

    /**
     * Pluggable VisitedFactory implementation
     */
    private VisitedFactory visitedFactory = new VisitedBitSetImpl();

    /**
     * Pluggable TopoOrderMappingFactory implementation
     */
    private TopoOrderMappingFactory<V> topoOrderFactory = new TopoVertexBiMap();

    /**
     * Construct a directed acyclic graph.
     *
     * @param edgeClass the edge class
     */
    public DirectedAcyclicGraph(Class<? extends E> edgeClass)
    {
        super(edgeClass);
        initialize();
    }

    /**
     * Construct a directed acyclic graph.
     *
     * @param ef the edge factory
     */
    public DirectedAcyclicGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
        initialize();
    }

    DirectedAcyclicGraph(
            Class<? extends E> arg0, VisitedFactory visitedFactory,
            TopoOrderMappingFactory<V> topoOrderFactory)
    {
        super(arg0);
        if (visitedFactory != null) {
            this.visitedFactory = visitedFactory;
        }
        if (topoOrderFactory != null) {
            this.topoOrderFactory = topoOrderFactory;
        }
        initialize();
    }

    /**
     * set the topoOrderMap based on the current factory, and create the comparator;
     */
    private void initialize()
    {
        topoOrderMap = topoOrderFactory.getTopoOrderMapping();
        topoComparator = new TopoComparator<>(topoOrderMap);
    }

    /**
     * iterator will traverse the vertices in topological order, meaning that for a directed graph G
     * = (V,E), if there exists a path from vertex va to vertex vb then va is guaranteed to come
     * before vertex vb in the iteration order.
     *
     * @return an iterator that will traverse the graph in topological order
     */
    public Iterator<V> iterator()
    {
        return new TopoIterator();
    }

    /**
     * Adds the vertex if it wasn't already in the graph, and puts it at the top of the internal
     * topological vertex ordering.
     *
     * @param v the vertex to add
     */
    @Override
    public boolean addVertex(V v)
    {
        boolean added = super.addVertex(v);

        if (added) {
            // add to the top
            ++maxTopoIndex;
            topoOrderMap.putVertex(maxTopoIndex, v);

            ++topologyUpdateCount;
        }

        return added;
    }

    /**
     * Adds the vertex if it wasn't already in the graph, and puts it either at the top or the
     * bottom of the topological ordering, depending on the value of addToTop. This may provide
     * useful optimizations for merging DirectedAcyclicGraphs that become connected.
     *
     * @param v the vertex to add
     * @param addToTop if true the vertex is added at the top of the topological ordering, if false
     *        at the bottom
     *
     * @return whether new vertex was added
     */
    public boolean addVertex(V v, boolean addToTop)
    {
        boolean added = super.addVertex(v);

        if (added) {
            int insertIndex;

            // add to the top
            if (addToTop) {
                insertIndex = ++maxTopoIndex;
            } else {
                insertIndex = --minTopoIndex;
            }
            topoOrderMap.putVertex(insertIndex, v);

            ++topologyUpdateCount;
        }
        return added;
    }


    public E addDagEdge(V fromVertex, V toVertex)
            throws CycleFoundException
    {
        updateDag(fromVertex, toVertex);

        return super.addEdge(fromVertex, toVertex);
    }


    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        E result;
        try {
            result = addDagEdge(sourceVertex, targetVertex);
        } catch (CycleFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    public boolean addDagEdge(V fromVertex, V toVertex, E e)
            throws CycleFoundException
    {
        if (e == null) {
            throw new NullPointerException();
        } else if (containsEdge(e)) {
            return false;
        }

        updateDag(fromVertex, toVertex);

        return super.addEdge(fromVertex, toVertex, e);
    }

    private void updateDag(V fromVertex, V toVertex)
            throws CycleFoundException
    {
        Integer lb = topoOrderMap.getTopologicalIndex(toVertex);
        Integer ub = topoOrderMap.getTopologicalIndex(fromVertex);

        if ((lb == null) || (ub == null)) {
            throw new IllegalArgumentException("vertices must be in the graph already!");
        }

        if (lb < ub) {
            Set<V> df = new HashSet<>();
            Set<V> db = new HashSet<>();

            // Discovery
            Region affectedRegion = new Region(lb, ub);
            Visited visited = visitedFactory.getInstance(affectedRegion);

            // throws CycleFoundException if there is a cycle
            dfsF(toVertex, df, visited, affectedRegion);

            dfsB(fromVertex, db, visited, affectedRegion);
            reorder(df, db, visited);
            ++topologyUpdateCount; // if we do a reorder, than the topology has
            // been updated
        }
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E edge)
    {
        boolean result;
        try {
            result = addDagEdge(sourceVertex, targetVertex, edge);
        } catch (CycleFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    @Override
    public boolean removeVertex(V v)
    {
        boolean removed = super.removeVertex(v);

        if (removed) {
            Integer topoIndex = topoOrderMap.removeVertex(v);

            // contract minTopoIndex as we are able
            if (topoIndex == minTopoIndex) {
                while ((minTopoIndex < 0) && (null == topoOrderMap.getVertex(minTopoIndex))) {
                    ++minTopoIndex;
                }
            }

            // contract maxTopoIndex as we are able
            if (topoIndex == maxTopoIndex) {
                while ((maxTopoIndex > 0) && (null == topoOrderMap.getVertex(maxTopoIndex))) {
                    --maxTopoIndex;
                }
            }

            ++topologyUpdateCount;
        }

        return removed;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> arg0)
    {
        return super.removeAllVertices(arg0);
    }


    private void dfsF(V vertex, Set<V> df, Visited visited, Region affectedRegion)
            throws CycleFoundException
    {
        int topoIndex = topoOrderMap.getTopologicalIndex(vertex);

        // Assumption: vertex is in the AR and so it will be in visited
        visited.setVisited(topoIndex);

        df.add(vertex);

        for (E outEdge : outgoingEdgesOf(vertex)) {
            V nextVertex = getEdgeTarget(outEdge);
            Integer nextVertexTopoIndex = topoOrderMap.getTopologicalIndex(nextVertex);

            if (nextVertexTopoIndex == affectedRegion.finish) {
                // reset visited
                try {
                    for (V visitedVertex : df) {
                        visited.clearVisited(topoOrderMap.getTopologicalIndex(visitedVertex));
                    }
                } catch (UnsupportedOperationException e) {
                    // okay, fine, some implementations (ones that automatically
                    // clear themselves out) don't work this way
                }
                throw new CycleFoundException();
            }

            // note, order of checks is important as we need to make sure the
            // vertex is in the affected region before we check its visited
            // status (otherwise we will be causing an
            // ArrayIndexOutOfBoundsException).
            if (affectedRegion.isIn(nextVertexTopoIndex)
                    && !visited.getVisited(nextVertexTopoIndex))
            {
                dfsF(nextVertex, df, visited, affectedRegion); // recurse
            }
        }
    }

    private void dfsB(V vertex, Set<V> db, Visited visited, Region affectedRegion)
    {
        // Assumption: vertex is in the AR and so we will get a topoIndex from
        // the map
        int topoIndex = topoOrderMap.getTopologicalIndex(vertex);
        visited.setVisited(topoIndex);

        db.add(vertex);

        for (E inEdge : incomingEdgesOf(vertex)) {
            V previousVertex = getEdgeSource(inEdge);
            Integer previousVertexTopoIndex = topoOrderMap.getTopologicalIndex(previousVertex);

            // note, order of checks is important as we need to make sure the
            // vertex is in the affected region before we check its visited
            // status (otherwise we will be causing an
            // ArrayIndexOutOfBoundsException).
            if (affectedRegion.isIn(previousVertexTopoIndex)
                    && !visited.getVisited(previousVertexTopoIndex))
            {
                // if prevousVertexTopoIndex != null, the vertex is in the
                // Affected Region according to our topoIndexMap

                dfsB(previousVertex, db, visited, affectedRegion);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void reorder(Set<V> df, Set<V> db, Visited visited)
    {
        List<V> topoDf = new ArrayList<>(df);
        List<V> topoDb = new ArrayList<>(db);

        Collections.sort(topoDf, topoComparator);
        Collections.sort(topoDb, topoComparator);

        // merge these suckers together in topo order

        SortedSet<Integer> availableTopoIndices = new TreeSet<>();

        // we have to cast to the generic type, can't do "new V[size]" in java
        // 5;
        V[] bigL = (V[]) new Object[df.size() + db.size()];
        int lIndex = 0; // this index is used for the sole purpose of pushing
        // into

        // the correct index of bigL

        // assume (for now) that we are resetting visited
        boolean clearVisited = true;

        for (V vertex : topoDb) {
            Integer topoIndex = topoOrderMap.getTopologicalIndex(vertex);

            // add the available indices to the set
            availableTopoIndices.add(topoIndex);

            bigL[lIndex++] = vertex;

            if (clearVisited) { // reset visited status if supported
                try {
                    visited.clearVisited(topoIndex);
                } catch (UnsupportedOperationException e) {
                    clearVisited = false;
                }
            }
        }

        for (V vertex : topoDf) {
            Integer topoIndex = topoOrderMap.getTopologicalIndex(vertex);

            // add the available indices to the set
            availableTopoIndices.add(topoIndex);
            bigL[lIndex++] = vertex;

            if (clearVisited) { // reset visited status if supported
                try {
                    visited.clearVisited(topoIndex);
                } catch (UnsupportedOperationException e) {
                    clearVisited = false;
                }
            }
        }

        lIndex = 0; // reusing lIndex
        for (Integer topoIndex : availableTopoIndices) {
            // assign the indexes to the elements of bigL in order
            V vertex = bigL[lIndex++]; // note the post-increment
            topoOrderMap.putVertex(topoIndex, vertex);
        }
    }

    /**
     * @param graph graph to look for ancestors in.
     * @param vertex the vertex to get the ancestors of.
     *
     * @return {@link Set} of ancestors of the vertex in the given graph.
     */
    public Set<V> getAncestors(DirectedAcyclicGraph<V, E> graph, V vertex)
    {

        EdgeReversedGraph<V, E> reversedGraph = new EdgeReversedGraph<>(graph);
        AbstractGraphIterator<V, E> iterator = new DepthFirstIterator<>(reversedGraph, vertex);
        Set<V> ancestors = new HashSet<>();

        // Do not add start vertex to result.
        if (iterator.hasNext()) {
            iterator.next();
        }

        while (iterator.hasNext()) {
            ancestors.add(iterator.next());
        }

        return ancestors;
    }

    /**
     * @param graph graph to look for descendants in.
     * @param vertex the vertex to get the descendants of.
     *
     * @return {@link Set} of descendants of the vertex in the given graph.
     */
    public Set<V> getDescendants(DirectedAcyclicGraph<V, E> graph, V vertex)
    {

        AbstractGraphIterator<V, E> iterator = new DepthFirstIterator<>(graph, vertex);
        Set<V> descendants = new HashSet<>();

        // Do not add start vertex to result.
        if (iterator.hasNext()) {
            iterator.next();
        }

        while (iterator.hasNext()) {
            descendants.add(iterator.next());
        }

        return descendants;
    }

    /**
     * For performance tuning, an interface for storing the topological ordering
     *
     * @param <V> the graph vertex type
     *
     * @author Peter Giles
     */
    public interface TopoOrderMapping<V>
            extends Serializable
    {
        /**
         * Add a vertex at the given topological index.
         *
         * @param index the topological index
         * @param vertex the vertex
         */
        void putVertex(Integer index, V vertex);

        /**
         * Get the vertex at the given topological index.
         *
         * @param index the topological index
         * @return vertex the vertex
         */
        V getVertex(Integer index);

        /**
         * Get the topological index of the given vertex.
         *
         * @param vertex the vertex
         * @return the index that the vertex is at, or null if the vertex isn't in the topological
         *         ordering
         */
        Integer getTopologicalIndex(V vertex);

        /**
         * Remove the given vertex from the topological ordering
         *
         * @param vertex the vertex
         * @return the index that the vertex was at, or null if the vertex wasn't in the topological
         *         ordering
         */
        Integer removeVertex(V vertex);

        /**
         * Remove all vertices from the topological ordering
         */
        void removeAllVertices();
    }

    /**
     * A factory for {@link TopoOrderMapping}.
     *
     * @param <V> the graph vertex type
     */
    public interface TopoOrderMappingFactory<V>
    {
        /**
         * Create a new instance of a {@link TopoOrderMapping}.
         *
         * @return a new instance of a {@link TopoOrderMapping}
         */
        TopoOrderMapping<V> getTopoOrderMapping();
    }

    /**
     * This interface allows specification of a strategy for marking vertices as visited (based on
     * their topological index, so the vertex type isn't part of the interface).
     */
    public interface Visited
    {
        /**
         * Mark the given topological index as visited
         *
         * @param index the topological index
         */
        void setVisited(int index);

        /**
         * Has the given topological index been visited?
         *
         * @param index the topological index
         * @return true if the given topological index been visited, false otherwise
         */
        boolean getVisited(int index);

        /**
         * Clear the visited state of the given topological index
         *
         * @param index the index
         *
         * @throws UnsupportedOperationException if the implementation doesn't support (or doesn't
         *         need) clearance. For example, if the factory vends a new instance every time, it
         *         is a waste of cycles to clear the state after the search of the Affected Region
         *         is done, so an UnsupportedOperationException *should* be thrown.
         */
        void clearVisited(int index)
                throws UnsupportedOperationException;
    }

    /**
     * Interface for a factory that vends visited implementations
     *
     * @author Peter Giles
     */
    public interface VisitedFactory
            extends Serializable
    {
        /**
         * Create a new instance of {@link Visited}.
         *
         * @param affectedRegion the affected region
         * @return a new instance of {@link Visited} for the affected region
         */
        Visited getInstance(Region affectedRegion);
    }

    /**
     * Note, this is a lazy and incomplete implementation, with assumptions that inputs are in the
     * given topoIndexMap
     *
     * @param <V> the graph vertex type
     *
     * @author Peter Giles
     */
    private static class TopoComparator<V>
            implements Comparator<V>, Serializable
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private TopoOrderMapping<V> topoOrderMap;

        public TopoComparator(TopoOrderMapping<V> topoOrderMap)
        {
            this.topoOrderMap = topoOrderMap;
        }

        @Override
        public int compare(V o1, V o2)
        {
            return topoOrderMap
                    .getTopologicalIndex(o1).compareTo(topoOrderMap.getTopologicalIndex(o2));
        }
    }

    /**
     * a dual HashMap implementation
     *
     * @author Peter Giles
     */
    private class TopoVertexBiMap
            implements TopoOrderMapping<V>, TopoOrderMappingFactory<V>
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private final Map<Integer, V> topoToVertex = new HashMap<>();
        private final Map<V, Integer> vertexToTopo = new HashMap<>();

        @Override
        public void putVertex(Integer index, V vertex)
        {
            topoToVertex.put(index, vertex);
            vertexToTopo.put(vertex, index);
        }

        @Override
        public V getVertex(Integer index)
        {
            return topoToVertex.get(index);
        }

        @Override
        public Integer getTopologicalIndex(V vertex)
        {
            return vertexToTopo.get(vertex);
        }

        @Override
        public Integer removeVertex(V vertex)
        {
            Integer topoIndex = vertexToTopo.remove(vertex);
            if (topoIndex != null) {
                topoToVertex.remove(topoIndex);
            }
            return topoIndex;
        }

        @Override
        public void removeAllVertices()
        {
            vertexToTopo.clear();
            topoToVertex.clear();
        }

        @Override
        public TopoOrderMapping<V> getTopoOrderMapping()
        {
            return this;
        }
    }

    /**
     * For performance and flexibility uses an ArrayList for topological index to vertex mapping,
     * and a HashMap for vertex to topological index mapping.
     *
     * @author Peter Giles
     */
    public class TopoVertexMap
            implements TopoOrderMapping<V>, TopoOrderMappingFactory<V>
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private final List<V> topoToVertex = new ArrayList<>();
        private final Map<V, Integer> vertexToTopo = new HashMap<>();

        @Override
        public void putVertex(Integer index, V vertex)
        {
            int translatedIndex = translateIndex(index);

            // grow topoToVertex as needed to accommodate elements
            while ((translatedIndex + 1) > topoToVertex.size()) {
                topoToVertex.add(null);
            }

            topoToVertex.set(translatedIndex, vertex);
            vertexToTopo.put(vertex, index);
        }

        @Override
        public V getVertex(Integer index)
        {
            return topoToVertex.get(translateIndex(index));
        }

        @Override
        public Integer getTopologicalIndex(V vertex)
        {
            return vertexToTopo.get(vertex);
        }

        @Override
        public Integer removeVertex(V vertex)
        {
            Integer topoIndex = vertexToTopo.remove(vertex);
            if (topoIndex != null) {
                topoToVertex.set(translateIndex(topoIndex), null);
            }
            return topoIndex;
        }

        @Override
        public void removeAllVertices()
        {
            vertexToTopo.clear();
            topoToVertex.clear();
        }

        @Override
        public TopoOrderMapping<V> getTopoOrderMapping()
        {
            return this;
        }

        /**
         * We translate the topological index to an ArrayList index. We have to do this because
         * topological indices can be negative, and we want to do it because we can make better use
         * of space by only needing an ArrayList of size |AR|.
         *
         * @return the ArrayList index
         */
        private int translateIndex(int index)
        {
            if (index >= 0) {
                return 2 * index;
            }
            return -1 * ((index * 2) - 1);
        }
    }

    /**
     * Region is an *inclusive* range of indices. Esthetically displeasing, but convenient for our
     * purposes.
     *
     * @author Peter Giles
     */
    public static class Region
            implements Serializable
    {
        private static final long serialVersionUID = 1L;

        public final int start;
        public final int finish;

        /**
         * Construct a new region.
         *
         * @param start the start of the region
         * @param finish the end of the region (inclusive)
         */
        public Region(int start, int finish)
        {
            if (start > finish) {
                throw new IllegalArgumentException("(start > finish): invariant broken");
            }
            this.start = start;
            this.finish = finish;
        }

        /**
         * Get the size of the region.
         *
         * @return the size of the region
         */
        public int getSize()
        {
            return (finish - start) + 1;
        }

        /**
         * Check if index is in the region.
         *
         * @param index the index to check
         * @return true if the index is in the region, false otherwise
         */
        public boolean isIn(int index)
        {
            return (index >= start) && (index <= finish);
        }
    }


    public static class VisitedBitSetImpl
            implements Visited, VisitedFactory
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private final BitSet visited = new BitSet();

        private Region affectedRegion;

        @Override
        public Visited getInstance(Region affectedRegion)
        {
            this.affectedRegion = affectedRegion;

            return this;
        }

        @Override
        public void setVisited(int index)
        {
            visited.set(translateIndex(index), true);
        }

        @Override
        public boolean getVisited(int index)
        {
            return visited.get(translateIndex(index));
        }

        @Override
        public void clearVisited(int index)
                throws UnsupportedOperationException
        {
            visited.clear(translateIndex(index));
        }

        /**
         * We translate the topological index to an ArrayList index. We have to do this because
         * topological indices can be negative, and we want to do it because we can make better use
         * of space by only needing an ArrayList of size |AR|.
         *
         * @return the ArrayList index
         */
        private int translateIndex(int index)
        {
            return index - affectedRegion.start;
        }
    }

    /**
     * This implementation seems to offer the best performance in most cases. It grows the internal
     * ArrayList as needed to be as large as |AR|, so it will be more memory intensive than the
     * HashSet implementation, and unlike the Array implementation, it will hold on to that memory
     * (it expands, but never contracts).
     *
     * @author Peter Giles
     */
    public static class VisitedArrayListImpl
            implements Visited, VisitedFactory
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private final List<Boolean> visited = new ArrayList<>();

        private Region affectedRegion;

        @Override
        public Visited getInstance(Region affectedRegion)
        {
            // Make sure visited is big enough
            int minSize = (affectedRegion.finish - affectedRegion.start) + 1;
            /* plus one because the region range is inclusive of both indices */

            while (visited.size() < minSize) {
                visited.add(Boolean.FALSE);
            }

            this.affectedRegion = affectedRegion;

            return this;
        }

        @Override
        public void setVisited(int index)
        {
            visited.set(translateIndex(index), Boolean.TRUE);
        }

        @Override
        public boolean getVisited(int index)
        {
            return visited.get(translateIndex(index));
        }

        @Override
        public void clearVisited(int index)
                throws UnsupportedOperationException
        {
            visited.set(translateIndex(index), Boolean.FALSE);
        }

        private int translateIndex(int index)
        {
            return index - affectedRegion.start;
        }
    }

    public static class VisitedHashSetImpl
            implements Visited, VisitedFactory
    {
        /**
         */
        private static final long serialVersionUID = 1L;

        private final Set<Integer> visited = new HashSet<>();

        @Override
        public Visited getInstance(Region affectedRegion)
        {
            visited.clear();
            return this;
        }

        @Override
        public void setVisited(int index)
        {
            visited.add(index);
        }

        @Override
        public boolean getVisited(int index)
        {
            return visited.contains(index);
        }

        @Override
        public void clearVisited(int index)
                throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }

    public static class VisitedArrayImpl
            implements Visited, VisitedFactory
    {
        private static final long serialVersionUID = 1L;

        private final boolean[] visited;

        private final Region region;

        /**
         * Constructs empty instance
         */
        public VisitedArrayImpl()
        {
            this(null);
        }

        /**
         * Construct an empty instance for a region.
         *
         * @param region the region
         */
        public VisitedArrayImpl(Region region)
        {
            if (region == null) { // make empty instance
                this.visited = null;
                this.region = null;
            } else { // fill in the needed pieces
                this.region = region;

                // initialized to all false by default
                visited = new boolean[region.getSize()];
            }
        }

        @Override
        public Visited getInstance(Region affectedRegion)
        {
            return new VisitedArrayImpl(affectedRegion);
        }

        @Override
        public void setVisited(int index)
        {
            visited[index - region.start] = true;
        }

        @Override
        public boolean getVisited(int index)
        {
            return visited[index - region.start];
        }

        @Override
        public void clearVisited(int index)
                throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Exception used in dfsF when a cycle is found
     *
     * @author Peter Giles
     */
    public static class CycleFoundException
            extends Exception
    {
        private static final long serialVersionUID = 5583471522212552754L;
    }

    /**
     * iterator which follows topological order
     *
     * @author Peter Giles
     */
    private class TopoIterator
            implements Iterator<V>
    {
        private int currentTopoIndex;
        private final long updateCountAtCreation;
        private Integer nextIndex = null;

        public TopoIterator()
        {
            updateCountAtCreation = topologyUpdateCount;
            currentTopoIndex = minTopoIndex - 1;
        }

        @Override
        public boolean hasNext()
        {
            if (updateCountAtCreation != topologyUpdateCount) {
                throw new ConcurrentModificationException();
            }

            nextIndex = getNextIndex();
            return nextIndex != null;
        }

        @Override
        public V next()
        {
            if (updateCountAtCreation != topologyUpdateCount) {
                throw new ConcurrentModificationException();
            }

            if (nextIndex == null) {
                // find nextIndex
                nextIndex = getNextIndex();
            }
            if (nextIndex == null) {
                throw new NoSuchElementException();
            }
            currentTopoIndex = nextIndex;
            nextIndex = null;
            return topoOrderMap.getVertex(currentTopoIndex); // topoToVertex.get(currentTopoIndex);
        }

        @Override
        public void remove()
        {
            if (updateCountAtCreation != topologyUpdateCount) {
                throw new ConcurrentModificationException();
            }

            V vertexToRemove;
            if (null != (vertexToRemove = topoOrderMap.getVertex(currentTopoIndex))) {
                topoOrderMap.removeVertex(vertexToRemove);
            } else {
                // should only happen if next() hasn't been called
                throw new IllegalStateException();
            }
        }

        private Integer getNextIndex()
        {
            for (int i = currentTopoIndex + 1; i <= maxTopoIndex; i++) {
                if (null != topoOrderMap.getVertex(i)) {
                    return i;
                }
            }
            return null;
        }
    }
}
