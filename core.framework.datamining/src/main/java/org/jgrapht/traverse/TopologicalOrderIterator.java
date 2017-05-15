package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.util.*;

/**
 * 拓扑排序
 * @param <V>
 * @param <E>
 */
public class TopologicalOrderIterator<V, E>
    extends CrossComponentIterator<V, E, Object>
{
    private Queue<V> queue;
    private Map<V, ModifiableInteger> inDegreeMap;

    public TopologicalOrderIterator(DirectedGraph<V, E> dg)
    {
        this(dg, new LinkedListQueue<>());
    }


    public TopologicalOrderIterator(DirectedGraph<V, E> dg, Queue<V> queue)
    {
        this(dg, queue, new HashMap<>());
    }

    // NOTE: This is a hack to deal with the fact that CrossComponentIterator
    // needs to know the start vertex in its constructor
    private TopologicalOrderIterator(
        DirectedGraph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        this(dg, initialize(dg, queue, inDegreeMap));
        this.queue = queue;
        this.inDegreeMap = inDegreeMap;

        // empty queue for non-empty graph would indicate presence of
        // cycles (no roots found)
        assert dg.vertexSet().isEmpty() || !queue.isEmpty();
    }

    // NOTE: This is intentionally private, because starting the sort "in the
    // middle" doesn't make sense.
    private TopologicalOrderIterator(DirectedGraph<V, E> dg, V start)
    {
        super(dg, start);
    }

    /**
     * @see CrossComponentIterator#isConnectedComponentExhausted()
     */
    @Override
    protected boolean isConnectedComponentExhausted()
    {
        return queue.isEmpty();
    }

    /**
     * @see CrossComponentIterator#encounterVertex(Object, Object)
     */
    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, null);
        decrementInDegree(vertex);
    }


    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
        decrementInDegree(vertex);
    }

    /**
     * @see CrossComponentIterator#provideNextVertex()
     */
    @Override
    protected V provideNextVertex()
    {
        return queue.remove();
    }

    /**
     * Decrements the in-degree of a vertex.
     *
     * @param vertex the vertex whose in-degree will be decremented.
     */
    private void decrementInDegree(V vertex)
    {
        ModifiableInteger inDegree = inDegreeMap.get(vertex);

        if (inDegree.value > 0) {
            inDegree.value--;

            if (inDegree.value == 0) {
                queue.offer(vertex);
            }
        }
    }

    private static <V, E> V initialize(
        DirectedGraph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        for (V vertex : dg.vertexSet()) {
            int inDegree = dg.inDegreeOf(vertex);
            inDegreeMap.put(vertex, new ModifiableInteger(inDegree));

            if (inDegree == 0) {
                queue.offer(vertex);
            }
        }

        if (queue.isEmpty()) {
            return null;
        } else {
            return queue.peek();
        }
    }

    private static class LinkedListQueue<T>
        extends LinkedList<T>
        implements Queue<T>
    {
        private static final long serialVersionUID = 4217659843476891334L;

        @Override
        public T element()
        {
            return getFirst();
        }

        @Override
        public boolean offer(T o)
        {
            return add(o);
        }

        @Override
        public T peek()
        {
            if (isEmpty()) {
                return null;
            }
            return getFirst();
        }

        @Override
        public T poll()
        {
            if (isEmpty()) {
                return null;
            }
            return removeFirst();
        }

        @Override
        public T remove()
        {
            return removeFirst();
        }
    }
}

// End TopologicalOrderIterator.java
