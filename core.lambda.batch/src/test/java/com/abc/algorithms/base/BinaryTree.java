package com.abc.algorithms.base;


import org.eclipse.jetty.util.ArrayQueue;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Queue;

/**
 * 二叉树
 * Created by admin on 2017/4/9.
 */
public class BinaryTree extends AbstractCollection {

    protected Object root;
    protected BinaryTree left, right, parent;
    protected int size;

    /**
     * 构造方法，根节点
     *
     * @param root
     */
    public BinaryTree(Object root) {
        this.root = root;
        this.size = 1;
    }

    /**
     * @param root
     * @param left
     * @param right
     */
    public BinaryTree(Object root, BinaryTree left, BinaryTree right) {
        this(root);
        if (left != null) {
            this.left = left;
            left.parent = this;
            this.size += left.size();
        }
        if (right != null) {
            this.right = right;
            right.parent = this;
            this.size += right.size();
        }
    }

    @Override
    public Iterator iterator() {

        return new Iterator() {
            private boolean rootDone;
            private Iterator leftIterator, rightIterator;

            @Override
            public boolean hasNext() {
                return !rootDone || (leftIterator != null && leftIterator.hasNext())
                        || (rightIterator != null && rightIterator.hasNext());
            }

            @Override
            public Object next() {
                if (rootDone) {
                    if (leftIterator != null && leftIterator.hasNext()) {
                        return leftIterator.next();
                    }
                    if (rightIterator != null && rightIterator.hasNext()) {
                        return rightIterator.next();
                    }
                    return null;
                }
                if (left != null) {
                    leftIterator = left.iterator();
                }
                if (right != null) {
                    rightIterator = right.iterator();
                }
                rootDone = true;
                return root;
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BinaryTree)) {
            return false;
        }
        BinaryTree tree = (BinaryTree) object;
        return (tree.root.equals(root)
                && tree.left.equals(left)
                && tree.right.equals(right)
                && tree.parent.equals(parent)
                && (tree.size == size)
        );
    }

    @Override
    public int hashCode() {
        return root.hashCode() + left.hashCode() + right.hashCode() + size;
    }

    public int leaves() {
        int leftLeaves = (left == null ? 0 : left.leaves());
        int rightLeaves = (right == null ? 0 : right.leaves());
        return leftLeaves + rightLeaves;
    }

    /////////////////////////////////////////////////////////////////////
    //遍历，前序，中序；后序遍历
    class DefualtIterator implements Iterator {
        protected boolean rootDone;
        protected Iterator leftIterator, rightIterator;

        @Override
        public boolean hasNext() {

            return !rootDone || (leftIterator != null && leftIterator.hasNext())
                    || (rightIterator != null && rightIterator.hasNext());
        }

        @Override
        public Object next() {
            if (rootDone) {
                if (leftIterator != null && leftIterator.hasNext()) {
                    return leftIterator.next();
                }
                if (rightIterator != null && rightIterator.hasNext()) {
                    return rightIterator.next();
                }
                return null;
            }
            if (left != null) {
                leftIterator = left.iterator();
            }
            if (right != null) {
                rightIterator = right.iterator();
            }
            rootDone = true;
            return root;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("二叉树遍历不支持删除方法");
        }
    }

    /**
     * 前序遍历
     *
     * @return
     */
    public class PreOrder extends DefualtIterator {

        public PreOrder() {
            if (left != null) {
                leftIterator = left.new PreOrder();
            }
            if (right != null) {
                rightIterator = right.new PreOrder();
            }
        }

        @Override
        public Object next() {
            if (!rootDone) {
                rootDone = true;
                return root;
            }
            if (leftIterator != null && leftIterator.hasNext()) {
                return leftIterator.next();
            }
            if (rightIterator != null && rightIterator.hasNext()) {
                return rightIterator.next();
            }
            return null;
        }
    }

    /**
     * InOrder 中序
     *
     * @return
     */
    public class InOrder extends DefualtIterator {

        public InOrder() {
            if (left != null) {
                leftIterator = left.new InOrder();
            }
            if (right != null) {
                rightIterator = right.new InOrder();
            }
        }

        @Override
        public Object next() {

            if (leftIterator != null && leftIterator.hasNext()) {
                return leftIterator.next();
            }
            if (!rootDone) {
                rootDone = true;
                return root;
            }
            if (rightIterator != null && rightIterator.hasNext()) {
                return rightIterator.next();
            }
            return null;
        }
    }

    /**
     * PostOrder 中序
     *
     * @return
     */
    public class PostOrder extends DefualtIterator {

        public PostOrder() {
            if (left != null) {
                leftIterator = left.new PostOrder();
            }
            if (right != null) {
                rightIterator = right.new PostOrder();
            }
        }

        @Override
        public Object next() {

            if (leftIterator != null && leftIterator.hasNext()) {
                return leftIterator.next();
            }
            if (rightIterator != null && rightIterator.hasNext()) {
                return rightIterator.next();
            }
            if (!rootDone) {
                rootDone = true;
                return root;
            }
            return null;
        }
    }

    /**
     * LevelOrder
     *
     * @return
     */
    public class LevelOrder extends DefualtIterator {
        Queue queue = new ArrayQueue();

        public boolean hasNext() {
            return (!rootDone) || !queue.isEmpty();
        }

        public Object next() {
            if (!rootDone) {
                if (left != null) {
                    queue.offer(left);
                }
                if (right != null) {
                    queue.offer(right);
                }
                rootDone = true;
                return root;
            }
            if (!queue.isEmpty()) {
                BinaryTree tree = (BinaryTree) queue.poll();
                if (tree.left != null) {
                    queue.offer(tree.left);
                }
                if (tree.right != null) {
                    queue.offer(tree.right);
                }
                return tree.root;
            }
            return null;
        }
    }
    ////////////////////////////////遍历方法/////////////////////////////////////

    public static void main(String[] args) {
        BinaryTree e = new BinaryTree("E");
        BinaryTree g = new BinaryTree("G");
        BinaryTree h = new BinaryTree("H");
        BinaryTree i = new BinaryTree("I");
        BinaryTree d = new BinaryTree("D", null, g);
        BinaryTree f = new BinaryTree("F", h, i);
        BinaryTree b = new BinaryTree("B", d, e);
        BinaryTree c = new BinaryTree("C", f, null);
        BinaryTree tree = new BinaryTree("A", b, c);
        System.out.println("tree is>\t");
        System.out.println(tree);
        BinaryTree.DefualtIterator iterator;
        System.out.println("前序");
        for (iterator = tree.new PreOrder(); iterator.hasNext(); ) {
            System.out.print(iterator.next() + "\t");
        }
        System.out.println("\n后序");
        for (iterator = tree.new PostOrder(); iterator.hasNext(); ) {
            System.out.print(iterator.next() + "\t");
        }
        System.out.println("\n中序");
        for (iterator = tree.new InOrder(); iterator.hasNext(); ) {
            System.out.print(iterator.next() + "\t");
        }
        System.out.println("\n层");
        for (iterator = tree.new LevelOrder(); iterator.hasNext(); ) {
            System.out.print(iterator.next() + "\t");
        }
    }
}
