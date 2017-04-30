package com.abc.basic.algoritms.tree;


import java.util.LinkedList;

public class BTreeNode {
    public BTreeNode father;
    public LinkedList<BTreeNode> sons=new LinkedList<BTreeNode>();
    public LinkedList<Integer> keys=new LinkedList<Integer>();
    public boolean leaf;
    public int position;

    public BTreeNode(){}

    public BTreeNode(int key){
        keys.add(key);
    }

    public BTreeNode(LinkedList<Integer> keys){
        this.keys=keys;
    }
}
