package org.jgrapht.alg.spanning;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Prim's_algorithm"> Prim's
 * algorithm</a> that finds a minimum spanning tree/forest subject to connectivity of the supplied
 * weighted undirected graph. The algorithm was developed by Czech mathematician V. Jarník and later
 * independently by computer scientist Robert C. Prim and rediscovered by E. Dijkstra.
 *
 */
public class PrimMinimumSpanningTree<V, E>
    implements SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> g;

    /**
     * Construct a new instance of the algorithm.
     * 
     * @param graph the input graph
     */
    public PrimMinimumSpanningTree(Graph<V, E> graph)
    {
        this.g = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpanningTree<E> getSpanningTree()
    {
        Set<E> minimumSpanningTreeEdgeSet = new HashSet<>(g.vertexSet().size());
        double spanningTreeWeight = 0d;
        //g，
        //([A, B, C, D, E], [{A,B}, {A,C}, {B,D}, {C,D}, {D,E}, {A,E}])

        /**
        0 = "A"
        1 = "B"
        2 = "C"
        3 = "D"
        4 = "E"
         **/
        Set<V> unspanned = new HashSet<>(g.vertexSet());

        while (!unspanned.isEmpty()) {
            Iterator<V> ri = unspanned.iterator();

            V root = ri.next();

            ri.remove();

            // Edges crossing the cut C = (S, V \ S), where S is set of
            // already spanned vertices

            PriorityQueue<E> dangling = new PriorityQueue<>(
                g.edgeSet().size(),//使用指定的初始容量创建一个 PriorityQueue，并根据指定的比较器对元素进行排序。
                (lop, rop) -> Double.valueOf(g.getEdgeWeight(lop)).compareTo(g.getEdgeWeight(rop)));
            //添加根节点相关边
            /**
            0 = {DefaultWeightedEdge@807} "(A : B)"
            1 = {DefaultWeightedEdge@809} "(A : C)"
            2 = {DefaultWeightedEdge@1007} "(A : E)"
            */
            dangling.addAll(g.edgesOf(root));
            //获取并移除此队列的头，如果此队列为空，则返回 null。
            for (E next; (next = dangling.poll()) != null;) {
                V currentV=g.getEdgeSource(next);
                //t为目标顶点
                V s, t = unspanned.contains(s = currentV) ? s : g.getEdgeTarget(next);

                // Decayed edges aren't removed from priority-queue so that
                // having them just ignored being encountered through min-max
                // traversal
                if (!unspanned.contains(t)) {//不包括则继续
                    continue;
                }
                //添加得边Set
                minimumSpanningTreeEdgeSet.add(next);
                spanningTreeWeight += g.getEdgeWeight(next);
                //此队列的头 是按指定排序方式确定的最小 元素。如果多个元素都是最小值，则头是其中一个元素——选择方法是任意的。
                // 队列获取操作 poll、remove、peek 和 element 访问处于队列头的元素。
                unspanned.remove(t);

                for (E e : g.edgesOf(t)) {
                    if (unspanned.contains(
                        g.getEdgeSource(e).equals(t) ? g.getEdgeTarget(e) : g.getEdgeSource(e)))
                    {   //添加
                        dangling.add(e);
                    }
                }
            }//
        }

        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }
}

// End PrimMinimumSpanningTree.java
