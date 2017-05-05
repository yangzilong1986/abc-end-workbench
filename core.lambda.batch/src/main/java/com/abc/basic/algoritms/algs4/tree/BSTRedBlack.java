package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.col.Queue;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.NoSuchElementException;

/**
 * 红黑树
 * 红连接将两个结点连接起来构成一个3-节点
 * 黑连接是普通的2-3数的普通连接
 *
 * 等价定义
 *      红连接均为左连接
 *      没有任何一个节点同时和两条红节点相连
 *      该树是完美黑色平衡的
 *
 *
 * 性质
 * 根节点为黑色BLACK
 * 叶子节点(nil)为黑色
 * 如果一个节点是红色的，那么它的两个子节点是黑色的
 *
 * 添加节点默认为红色
 *
 * @param <Key>
 * @param <Value>
 */
public class BSTRedBlack<Key extends Comparable<Key>, Value> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private Value val;         // associated data
        private Node left, right;  // links to left and right subtrees
        //有其父节点指定它的连接颜色
        private boolean color;     // color of parent link
        private int size;          // subtree count

        public Node(Key key, Value val, boolean color, int size) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.size = size;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public BSTRedBlack() {
    }

   /***************************************************************************
    *  Node helper methods.
    ***************************************************************************/
    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) {//默认为黑色
            return false;
        }
        return x.color == RED;
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) {
            return 0;
        }
        return x.size;
    } 

    public int size() {
        return size(root);
    }

   /**
     * Is this symbol table empty?
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }


   /***************************************************************************
    *  Standard BST search.
    ***************************************************************************/
    public Value get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }
        return get(root, key);
    }

    // value associated with the given key in subtree rooted at x; null if no such key
    private Value get(Node x, Key key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;
            }else if (cmp > 0) {
                x = x.right;
            }else              {
                return x.val;
            }
        }
        return null;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

   /***************************************************************************
    *  Red-black tree insertion.
    ***************************************************************************/
    public void put(Key key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("first argument to put() is null");
        }
        if (val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
        //插入数据默认为黑色
        root.color = BLACK;
        // assert check();
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node h, Key key, Value val) { 
        if (h == null) {//子结点b
            //新建的连接为红色，即总是用红色结点连接到树中
            return new Node(key, val, RED, 1);
        }

        //插入的key和原有节点node的key比较
        int cmp = key.compareTo(h.key);
        //左右两个树下节点
        if(cmp < 0) {//小于树中的节点
            h.left  = put(h.left,  key, val);//left:b
        } else if (cmp > 0) {//新键大于父节点
            h.right = put(h.right, key, val);
        }else {
            h.val   = val;
        }

        // fix-up any right-leaning links
        //右孩子是红色，左孩子不是红色，则左旋转
        //右边是红色新增的键是右边，即大于父节点的键，但是左边不是红色，即为空或者有值
        //左孩子是红色，左孩子的左孩子是红色，则右旋转
        //c-b树中插入a，如下图所示
        //          c-h回退到此时旋转             b
        //  黑色 |     \红色边
        //     a      b       左旋转        a           c
        //
        //当插入完a后，当递归出栈，到c节点，即根节点时
        if (isRed(h.right) && !isRed(h.left)) {//h.left a 右红左不红
            h = rotateLeft(h);//左旋转
        }
        //左孩子是红色，左孩子的左孩子是红色，则右旋转
        //c-b树中插入a，如下图所示
        //        c-回退到此时旋转          b
        //       |红色边
        //      b            右旋转   a           c
        //     | 红色边
        //    a-插入值
        //当插入完a后，当递归出栈，到c节点，即根节点时
        if (isRed(h.left)  &&  isRed(h.left.left)) {//左红，左左红
            h = rotateRight(h);
        }
        //左右均为红色
        if (isRed(h.left)  &&  isRed(h.right))   {//左右红
            flipColors(h);
        }
        h.size = size(h.left) + size(h.right) + 1;

        return h;
    }

    // flip the colors of a node and its two children
    /**
     * 颜色转换
     * @param h
     */
    private void flipColors(Node h) {
        //颜色取反
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // make a left-leaning link lean to the right
    /**
     * 旋转之后，返回结点为红色节点
     * @param h
     * @return
     */
    private Node rotateRight(Node h) {
        // assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    /**
     * 旋转之后，返回结点为红色节点
     * @param h
     * @return
     */
    private Node rotateLeft(Node h) {
        // assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;//返回的节点
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }
    /***************************************************************************
    *  Red-black tree deletion.
    ***************************************************************************/
    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("BST underflow");
        }

        // if both children of root are black, set root to red
        //左右都不是红色
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = deleteMin(root);
        if (!isEmpty()) {
            root.color = BLACK;
        }
        // assert check();
    }

    // delete the key-value pair with the minimum key rooted at h
    private Node deleteMin(Node h) {
        //左边最小，没有左结点则返回，它是递归退出的条件
        if (h.left == null) {
            return null;
        }
        //左孩子和左孩子的左孩子都不是红色
        //               c
        //          b
        //      a
        if (!isRed(h.left) && !isRed(h.left.left)) {//左不红左左不红
            h = moveRedLeft(h);
        }
        h.left = deleteMin(h.left);
        return balance(h);
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);
        //          c
        //              e
        //          d
        //上面树结构则，则执行下面操作
        flipColors(h);
        if (isRed(h.right.left)) {//右左红
            h.right = rotateRight(h.right);//左转
            h = rotateLeft(h);//右转
            flipColors(h);
        }
        return h;
    }

    // restore red-black tree invariant
    private Node balance(Node h) {
        // assert (h != null);
        //如果右边是红色，则左转
        //  b
        //      c
        if (isRed(h.right)){//右红
            h = rotateLeft(h);
        }
        //如果左边，左结点的左结点都是红色，则右转
        //             c
        //        b
        //  a
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        //左右为红，
        if (isRed(h.left) && isRed(h.right))     {
            flipColors(h);
        }

        h.size = size(h.left) + size(h.right) + 1;
        return h;
    }
    ////////////////////////////////////////////////////////////////

    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("BST underflow");
        }

        // if both children of root are black, set root to red
        //         root
        //     a           f
       if (!isRed(root.left) && !isRed(root.right)) {//左右都不红
            root.color = RED;
        }
        root = deleteMax(root);
        if (!isEmpty()) {
            root.color = BLACK;
        }
        // assert check();
    }

    // delete the key-value pair with the maximum key rooted at h
    private Node deleteMax(Node h) {
        //删除最大值，最大值一直在右边
        //          b
        //      a
        if (isRed(h.left)) {
            h = rotateRight(h);
        }
        if (h.right == null) {
            return null;
        }
        //    d
        //         f
        //      e
        //删除最小值
        //if (!isRed(h.left) && !isRed(h.left.left))
        if (!isRed(h.right) && !isRed(h.right.left)) {
            h = moveRedRight(h);
        }
        h.right = deleteMax(h.right);

        return balance(h);
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        //            a
        //     c
        //e
        flipColors(h);
        if (isRed(h.left.left)) {//左左红
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }
    /////////////////////////////////////////////////
    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to delete() is null");
        }
        if (!contains(key)) {
            return;
        }

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = delete(root, key);
        if (!isEmpty()) {
            root.color = BLACK;
        }
        // assert check();
    }

    // delete the key-value pair with the given key rooted at h
    private Node delete(Node h, Key key) { 
        // assert get(h, key) != null;
        //             d
        //      b              f
        //  a       c      e       g
        //删除a时
        if (key.compareTo(h.key) < 0)  {//删除值小于树结点左边
            //if (!isRed(h.left) && !isRed(h.left.left)) {
            //          d-h
            //      b
            //  a
            if (!isRed(h.left) && !isRed(h.left.left)) {//小于树中的key
                h = moveRedLeft(h);//左转
            }//
            h.left = delete(h.left, key);
        }else {//删除e,删除的key大于树中键
            if (isRed(h.left)) {//左红
                h = rotateRight(h);
            }
            //             d
            //      b              f
            //  a       c      e       g
            //删除e结点，右旋转根结点d
            //            b
            //      a               d
            //                c              f
            //                          e          g
            if (key.compareTo(h.key) == 0 && (h.right == null)) {

                return null;//如果是f结点，则返回
            }
            //       h
            //                b
            //          c
            //
            if (!isRed(h.right) && !isRed(h.right.left)) {
                //h.left.left
                h = moveRedRight(h);
            }
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                // h.val = get(h.right, min(h.right).key);
                // h.key = min(h.right).key;
                h.right = deleteMin(h.right);
            }else {
                h.right = delete(h.right, key);
            }
        }
        return balance(h);
    }

   /***************************************************************************
    *  Utility functions.
    ***************************************************************************/
    /**
     * Returns the height of the BST (for debugging).
     * @return the height of the BST (a 1-node tree has height 0)
     */
    public int height() {
        return height(root);
    }
    private int height(Node x) {
        if (x == null) {
            return -1;
        }
        return 1 + Math.max(height(x.left), height(x.right));
    }

   /***************************************************************************
    *  Ordered symbol table methods.
    ***************************************************************************/
    /**
     * Returns the smallest key in the symbol table.
     * @return the smallest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public Key min() {
        if (isEmpty()) {
            throw new NoSuchElementException("called min() with empty symbol table");
        }
        return min(root).key;
    } 

    // the smallest key in subtree rooted at x; null if no such key
    private Node min(Node x) { 
        // assert x != null;
        if (x.left == null) {
            return x;
        }else {
            return min(x.left);
        }
    } 

    /**
     * Returns the largest key in the symbol table.
     * @return the largest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public Key max() {
        if (isEmpty()) {
            throw new NoSuchElementException("called max() with empty symbol table");
        }
        return max(root).key;
    } 

    // the largest key in the subtree rooted at x; null if no such key
    private Node max(Node x) { 
        // assert x != null;
        if (x.right == null) {
            return x;
        } else{
            return max(x.right);
        }
    } 

    public Key floor(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to floor() is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("called floor() with empty symbol table");
        }
        Node x = floor(root, key);
        if (x == null) {
            return null;
        }else {
            return x.key;
        }
    }    

    // the largest key in the subtree rooted at x less than or equal to the given key
    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            return x;
        }
        if (cmp < 0)  {
            return floor(x.left, key);
        }
        Node t = floor(x.right, key);
        if (t != null) {
            return t;
        } else  {
            return x;
        }
    }

    public Key ceiling(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to ceiling() is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("called ceiling() with empty symbol table");
        }
        Node x = ceiling(root, key);
        if (x == null) {
            return null;
        }else {
            return x.key;
        }
    }

    // the smallest key in the subtree rooted at x greater than or equal to the given key
    private Node ceiling(Node x, Key key) {  
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0)  return ceiling(x.right, key);
        Node t = ceiling(x.left, key);
        if (t != null) return t; 
        else           return x;
    }

    public Key select(int k) {
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("called select() with invalid argument: " + k);
        }
        Node x = select(root, k);
        return x.key;
    }

    // the key of rank k in the subtree rooted at x
    private Node select(Node x, int k) {
        // assert x != null;
        // assert k >= 0 && k < size(x);
        int t = size(x.left); 
        if (t > k)
            return select(x.left,  k);
        else if (t < k)
            return select(x.right, k-t-1);
        else
            return x;
    } 

    public int rank(Key key) {
        if (key == null)
            throw new IllegalArgumentException("argument to rank() is null");
        return rank(key, root);
    } 

    // number of keys less than key in the subtree rooted at x
    private int rank(Key key, Node x) {
        if (x == null)
            return 0;
        int cmp = key.compareTo(x.key); 
        if (cmp < 0)
            return rank(key, x.left);
        else if (cmp > 0)
            return 1 + size(x.left) + rank(key, x.right);
        else
            return size(x.left);
    } 

   /***************************************************************************
    *  Range count and range search.
    ***************************************************************************/
    public Iterable<Key> keys() {
        if (isEmpty()) return new Queue<Key>();
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null)
            throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null)
            throw new IllegalArgumentException("second argument to keys() is null");

        Queue<Key> queue = new Queue<Key>();
        // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
        keys(root, queue, lo, hi);
        return queue;
    } 

    // add the keys between lo and hi in the subtree rooted at x
    // to the queue
    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) { 
        if (x == null) return; 
        int cmplo = lo.compareTo(x.key); 
        int cmphi = hi.compareTo(x.key); 
        if (cmplo < 0)
            keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >= 0)
            queue.enqueue(x.key);
        if (cmphi > 0)
            keys(x.right, queue, lo, hi);
    } 


    public int size(Key lo, Key hi) {
        if (lo == null)
            throw new IllegalArgumentException("first argument to size() is null");
        if (hi == null)
            throw new IllegalArgumentException("second argument to size() is null");

        if (lo.compareTo(hi) > 0)
            return 0;
        if (contains(hi))
            return rank(hi) - rank(lo) + 1;
        else
            return rank(hi) - rank(lo);
    }


   /***************************************************************************
    *  Check integrity of red-black tree data structure.
    ***************************************************************************/
    private boolean check() {
        if (!isBST())            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent()) StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent()) StdOut.println("Ranks not consistent");
        if (!is23())             StdOut.println("Not a 2-3 tree");
        if (!isBalanced())       StdOut.println("Not balanced");
        return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0)
            return false;
        if (max != null && x.key.compareTo(max) >= 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    } 

    // are the size fields correct?
    private boolean isSizeConsistent() {
        return isSizeConsistent(root);
    }
    private boolean isSizeConsistent(Node x) {
        if (x == null)
            return true;
        if (x.size != size(x.left) + size(x.right) + 1)
            return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    } 

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (Key key : keys())
            if (key.compareTo(select(rank(key))) != 0) return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() {
        return is23(root);
    }
    private boolean is23(Node x) {
        if (x == null)
            return true;
        if (isRed(x.right))
            return false;
        if (x != root && isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    } 

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() { 
        int black = 0;     // number of black links on path from root to min
        Node x = root;
        while (x != null) {
            if (!isRed(x))
                black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(Node x, int black) {
        if (x == null)
            return black == 0;
        if (!isRed(x))
            black--;
        return isBalanced(x.left, black) && isBalanced(x.right, black);
    } 

    public static void main(String[] args) {
        BSTRedBlack<String, Integer> st = new BSTRedBlack<String, Integer>();
        String key="a";
        int i=1;
        st.put(key, i);

        key="c";
        i=2;
        st.put(key, i);

        key="d";
        i=3;
        st.put(key, i);

        key="h";
        i=5;
        st.put(key, i);

        key="f";
        i=4;
        st.put(key, i);

        key="j";
        i=6;
        st.put(key, i);

        key="m";
        i=7;
        st.put(key, i);

        key="z";
        i=9;
        st.put(key, i);

        for (String s : st.keys())
            StdOut.println(s + " " + st.get(s));
        StdOut.println();
    }
}