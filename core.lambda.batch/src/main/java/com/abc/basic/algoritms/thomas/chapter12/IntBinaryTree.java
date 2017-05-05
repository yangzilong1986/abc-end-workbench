package com.abc.basic.algoritms.thomas.chapter12;

import java.util.IdentityHashMap;
import java.util.NoSuchElementException;
import java.util.Random;

public class IntBinaryTree {
//    private static final boolean CHECK_INVARIANT = true;
    private static final boolean CHECK_INVARIANT = false;
    private Node root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private Node minimumNode(Node node) {
        assert node != null;
        while (node.left != null) {//最左边节点
            node = node.left;
        }
        return node;
    }

    /**
     * 查找最小节点
     *
     * @return
     */
    public int minimum() {
        if (size == 0) {
            throw new IllegalStateException("no data");
        }
        return minimumNode(root).data;
    }

    private Node maximumNode(Node node) {
        assert node != null;
        while (node.right != null) {//最右边节点
            node = node.right;
        }
        return node;
    }

    public int maximum() {
        if (size == 0) {
            throw new IllegalStateException("no data");
        }
        return maximumNode(root).data;
    }

    /**
     * 按中序遍历的次序查找         |
     * 后继，如果有后继             \
     *                               \
     *                                \
     * @param node
     * @return
     */
    private Node successorNode(Node node) {
        assert node != null;
        if (node.right != null) {//右边不为空，则遍历
            return minimumNode(node.right);
        } else {//
            Node parent = node.parent;
            while (parent != null && parent.right == node) {
                node = parent;//一直向上，直到遇到左，即从大到小的转折点
                parent = node.parent;
            }
            return parent;
        }
    }

    /**
     * 前继,上升，前序遍历
     *
     * @return
     */
    public IntIterator ascendingIterator() {
        return new IntIterator() {
            private Node next = root == null ? null : minimumNode(root);

            public boolean hasNext() {
                return next != null;
            }

            public int next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                int result = next.data;
                next = successorNode(next);
                return result;
            }
        };
    }

    /**
     * 后续遍历，上升   \
     *                  |
     *                 |
     *                |
     * @param node
     * @return
     */
    private Node predecessorNode(Node node) {
        assert node != null;
        if (node.left != null) {//有右节点，则返回
            return maximumNode(node.left);
        } else {//没有右节点时
            Node parent = node.parent;
            while (parent != null && parent.left == node) {
                node = parent;
                parent = node.parent;
            }
            return parent;
        }
    }

    /**
     * 降序，后续访问
     * @return
     */
    public IntIterator desendingIterator() {
        return new IntIterator() {
            private Node next = root == null ? null : maximumNode(root);

            public boolean hasNext() {
                return next != null;
            }

            public int next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                int result = next.data;
                next = predecessorNode(next);
                return result;
            }
        };
    }

    Node searchNode(int d) {
        return searchNode(root, d);
    }

    private Node searchNode(Node node, int d) {
        while (node != null && node.data != d) {
            if (d < node.data) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node;
    }

    public void insert(int d) {
        Node prev = null;
        Node cur = root;
        while (cur != null) {//根节点不为空
            prev = cur;
            if (d < cur.data) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        if (prev == null) {//根节点为空
            root = new Node(d);
        } else {
            //插入的节点
            //prev为查询的插入位置
            Node newNode = new Node(d, prev);
            if (d < prev.data) {
                prev.left = newNode;
            } else {
                prev.right = newNode;
            }
        }
        size++;

        if (CHECK_INVARIANT) {
            checkInvarient();
        }
    }

    public boolean delete(int d) {
        Node node = searchNode(d);
        if (node != null) {//找到节点
            delete(node);
            if (CHECK_INVARIANT) {
                checkInvarient();
            }
            return true;
        } else {
            return false;
        }
    }

    //        q
    //                 z
    //             l        r
    //                  y
    //                     x
    private void delete(Node node) {
        assert node != null;//node为z时
        Node d; // d为操作的节点
        if (node.left == null || node.right == null) {
            d = node;//如l、r。x
        } else {
            //d为要删除的结点的后继节点，z的所有不为空，
            // 查找z的后继，z的后继为y
            d = successorNode(node);
        }
        //如果是l，r，x则d为删除节点，否则为后继节点
        //如果删除l，r，x即有一个节点时，此时如果有左节点则取左节点，否则取右节点
        //如果删除的节点z为含有两个子节点时，找到其后继，如z的后继为y
        Node c = d.left != null ? d.left : d.right; // c为要链接到的子结点
        if (c != null) {//设置删除节点孩子的父节点
            //此不为空，如删除r时，则c为y，r的父节点z，设置y的父节点为z
            //如果删除z时，设置其后继的孩子y节点的孩子x的父节点为r
             c.parent = d.parent;
        }

        //d没有父节点时，则为根节点，此时c为新的父节点
        if (d.parent == null) {
            root = c;
        } else {                                       //  z              z
            //删除的为l，r，x，                  如图         r                 y
            //删除节点为左节点，如l的父节点z，           y（上面把y的父指向了z）
            //如删除r时，c为y，d为r                          x                x
            //此时，d.parent.left为z，
            if (d.parent.left == d) {
                d.parent.left = c;//
            } else {
                d.parent.right = c;
            }
        }

        if (d != node) {
            node.data = d.data;
        }
        size--;
    }

    private void checkInvarient() {
        IdentityHashMap<Node, Node> set = new IdentityHashMap<Node, Node>();
        checkLeftRightParentPointer(root, set);

        if (set.size() != size) {
            throw new IllegalStateException(String.format("actual size(%d)" +
                    " is not the expected size(%d)", set.size(), size));
        }
    }

    /**
     * 检查left, right, parent指针之间的引用关系是否正确。
     */
    private void checkLeftRightParentPointer(Node node, IdentityHashMap<Node, Node> set) {
        if (node == null) return;
        if (set.containsKey(node)) {
            throw new IllegalStateException("circular reference detected!");
        }
        set.put(node, node);

        Node l = node.left;
        Node r = node.right;
        if (l != null) {
            if (l.parent != node) {
                throw new IllegalStateException("left-parent relation violated");
            }
            if (l.data > node.data) {
                throw new IllegalStateException(String.format("left node(%s) > parent node(%s)", l, node));
            }
        }
        if (r != null) {
            if (r.parent != node) {
                throw new IllegalStateException("right-parent relation violated");
            }
            if (r.data < node.data) {
                throw new IllegalStateException(String.format("right node(%s) < parent node(%s)", r, node));
            }
        }

        checkLeftRightParentPointer(node.left, set);
        checkLeftRightParentPointer(node.right, set);
    }

    public String toString() {
        if (root == null) return "[empty]";
        StringBuilder sb = new StringBuilder();
        Node node = minimumNode(root);
        while (node != null) {
            sb.append(String.format("%d: %s %s %s%n", node.data,
                    node.left == null ? "-" : node.left.data,
                    node.right == null ? "-" : node.right.data,
                    node.parent == null ? "-" : node.parent.data));
            node = successorNode(node);
        }
        return sb.toString();
    }

    private static class Node {
        private int data;
        private Node left;
        private Node right;
        private Node parent;

        public Node(int d) {
            data = d;
        }

        public Node(int d, Node p) {
            data = d;
            parent = p;
        }

        public Node(int d, Node p, Node l, Node r) {
            data = d;
            parent = p;
            left = l;
            right = r;
        }

        public String toString() {
            return Integer.toString(data);
        }
    }

    public static void main(String[] args) {
        IntBinaryTree btree = new IntBinaryTree();

        int[] data = new int[]{8, 9, 6, 5, 13, 7};
        for (int i = 0; i < data.length; i++) {
            btree.insert(data[i]);
        }

//        System.out.println("tree: " + btree);

        System.out.println("minimum: " + btree.minimum());
        System.out.println("maximum: " + btree.maximum());

        System.out.println("ascendingIterator");
        for (IntIterator itor = btree.ascendingIterator(); itor.hasNext(); ) {
            System.out.println(itor.next());
        }

        System.out.println("desendingIterator");
        for (IntIterator itor = btree.desendingIterator(); itor.hasNext(); ) {
            System.out.println(itor.next());
        }

        btree.delete(6);
        System.out.println("ascendingIterator");
        for (IntIterator itor = btree.ascendingIterator(); itor.hasNext(); ) {
            System.out.println(itor.next());
        }


        btree = new IntBinaryTree();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            btree.insert(random.nextInt(200));
        }
        for (int i = 0; i < 200; i++) {
            btree.delete(random.nextInt(200));
        }
    }
}
