package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.util.*;

public class DepthFirstIterator<V, E>
    extends CrossComponentIterator<V, E, CrossComponentIterator.VisitColor>
{

    //哨兵
    public static final Object SENTINEL = new Object();

    /**
     * @see #getStack
     */
    private Deque<Object> stack = new ArrayDeque<>();

    private transient TypeUtil<V> vertexTypeDecl = null;

    public DepthFirstIterator(Graph<V, E> g)
    {
        this(g, null);
    }

    public DepthFirstIterator(Graph<V, E> g, V startVertex)
    {
        super(g, startVertex);
    }

//    BreadthFirstIterator方法
//    protected boolean isConnectedComponentExhausted()
//    {
//        return queue.isEmpty();
//    }

    @Override
    protected boolean isConnectedComponentExhausted()
    {
        for (;;) {
            if (stack.isEmpty()) {
                return true;
            }
            if (stack.getLast() != SENTINEL) {
                // Found a non-sentinel.
                return false;
            }

            // Found a sentinel: pop it, record the finish time,
            // and then loop to check the rest of the stack.
            /**
             获取并移除此双端队列的最后一个元素。
             此方法与 pollLast 唯一的不同在于：如果此双端队列为空，它将抛出一个异常。
             返回：此双端队列的尾部
             抛出：NoSuchElementException - 如果此双端队列为空
             */
            stack.removeLast();

            // This will pop corresponding vertex to be recorded as finished.
            recordFinish();
        }
    }

//    BreadthFirstIterator方法
//    protected void encounterVertex(V vertex, E edge)
//    {
//        putSeenData(vertex, null);
//        queue.add(vertex);
//    }
    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, VisitColor.WHITE);
        /**
        将指定元素插入此双端队列的末尾（如果可以直接这样做而不违反容量限制）。
         在使用有容量限制的双端队列时，通常首选 offerLast(E) 方法。
        此方法等效于 add(E)。
        参数：
        e - 要添加的元素
        抛出：
        IllegalStateException - 如果此时由于容量限制而无法添加元素
        ClassCastException - 如果指定元素的类不允许将它添加此双端队列
        NullPointerException - 如果指定元素为 null，并且此双端队列不允许 null 元素
        IllegalArgumentException - 如果指定元素的某些属性不允许将它添加到此双端队列
        **/
        stack.addLast(vertex);
    }

//    BreadthFirstIterator方法
//    @Override
//    protected void encounterVertexAgain(V vertex, E edge)
//    {
//    }
    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
        VisitColor color = getSeenData(vertex);
        if (color != VisitColor.WHITE) {//白色
            // We've already visited this vertex; no need to mess with the
            // stack (either it's BLACK and not there at all, or it's GRAY
            // and therefore just a sentinel).
            return;
        }

        // Since we've encountered it before, and it's still WHITE, it
        // *must* be on the stack. Use removeLastOccurrence on the
        // assumption that for typical topologies and traversals,
        // it's likely to be nearer the top of the stack than
        // the bottom of the stack.
        /**
         从此双端队列移除最后一次出现的指定元素。如果此双端队列不包含该元素，则不作更改。
         更正式地说，移除最后一个满足 (o==null ? e==null : o.equals(e)) 的元素 e（如果存在这样的元素）。
         如果此双端队列包含指定的元素（或者此双端队列由于调用而发生了更改），则返回 true。
         参数：
         o - 要从此双端队列移除的元素（如果存在）
         返回：
         如果由于此调用而移除了一个元素，则返回 true
         抛出：
         ClassCastException - 如果指定元素的类与此双端队列不兼容（可选）
         NullPointerException - 如果指定元素为 null，并且此双端队列不允许 null 元素（可选）
         */
        boolean found = stack.removeLastOccurrence(vertex);
        assert (found);
        stack.addLast(vertex);
    }

//    BreadthFirstIterator方法
//    @Override
//    protected V provideNextVertex()
//    {
//        return queue.removeFirst();
//    }
    @Override
    protected V provideNextVertex()
    {
        V v;
        for (;;) {
            Object o = stack.removeLast();
            if (o == SENTINEL) {//
                // This is a finish-time sentinel we previously pushed.
                recordFinish();
                // Now carry on with another pop until we find a non-sentinel
            } else {
                // Got a real vertex to start working on
                v = TypeUtil.uncheckedCast(o, vertexTypeDecl);
                break;
            }
        }

        // Push a sentinel for v onto the stack so that we'll know
        // when we're done with it.
        stack.addLast(v);
        stack.addLast(SENTINEL);
        putSeenData(v, VisitColor.GRAY);//灰色
        return v;
    }

    private void recordFinish()
    {
        V v = TypeUtil.uncheckedCast(stack.removeLast(), vertexTypeDecl);
        putSeenData(v, VisitColor.BLACK);//黑色
        finishVertex(v);
    }

    public Deque<Object> getStack()
    {
        return stack;
    }
}

// End DepthFirstIterator.java
