package com.abc.basic.algoritms.base.graph.traverse;


import com.abc.basic.algoritms.base.graph.DirectedGraph;
import org.jgrapht.util.ModifiableInteger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    private TopologicalOrderIterator(
            DirectedGraph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        this(dg, initialize(dg, queue, inDegreeMap));
        this.queue = queue;
        this.inDegreeMap = inDegreeMap;

        assert dg.vertexSet().isEmpty() || !queue.isEmpty();
    }

    private TopologicalOrderIterator(DirectedGraph<V, E> dg, V start)
    {
        super(dg, start);
    }


    @Override
    protected boolean isConnectedComponentExhausted()
    {
        return queue.isEmpty();
    }

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

    @Override
    protected V provideNextVertex()
    {
        return queue.remove();
    }

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
