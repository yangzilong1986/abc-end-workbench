package com.abc.algorithms.base;

/**
 * Created by admin on 2017/4/10.
 */
public class TreeUtil {
    public static class Tree{
        Comparable key;
        Tree left,right,root;
    }

    public static Tree search(Tree x,Comparable key){
        //中序遍历
        if(x==null||key==x.key){
            return x;
        }
        if(key.compareTo(x.key)<0){
            return search(x.left,key);
        }else{
            return search(x.right,key);
        }
    }

    public static void insert(Tree tree,Tree z){
        Tree y=null;
        Tree x=tree.root;
        while (x!=null){
            y=x;
            if(z.key.compareTo(x.key)<0){
                x=x.left;
            }else{
                x=x.right;
            }
        }
        z.root=y;
        if(y==null){
            tree.root=z;
        }else if(z.key.compareTo(y.key)<0){
            y.left=z;
        }else{
            y.right=z;
        }
    }
}
