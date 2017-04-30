package com.abc.basic.algoritms.tree;


import java.util.LinkedList;
import java.util.List;

/**
 *每个叶结点具有相同的深度。树的高度h 树的度为t
 *每个结点的关键字  非根至少t-1个，至多2t-1 个,根至少1个
 *非根非叶子结点最少t子女，最多2t个子女
 *根至少2两个子女,最多2t个子女
 */
public class BTree {
    public static Integer M=3;
    private BTreeNode root;

    public static void main(String args[]){
        BTree tree=new BTree();
        String str="";

        int[] keys=new int[3];
        int j=0;
        for(int i=1;i<=10000;i++){
            int key=(int) (Math.random()*100);
            if(i==10||i==24||i==30){
                keys[j]=key;
                j++;
            }
            tree.add(key);
        }
        tree.printTree();
        for(int key:keys){
            tree.delete(key);
            System.out.println(key);
            tree.printTree();
        }
    }

    public void printTree(){
        String keys="";
        keys+=this.printNode(root);
        System.out.println(keys);
    }

    public String printNode(BTreeNode node){
        String str=" \n结点:位置 "+node.position+" ";
        str+=node.keys.toString();
        if(node.sons.size()>0){
            for(BTreeNode n:node.sons){
                str+=printNode(n);
            }
        }
        return str;
    }

    public void add(int key){
        if(root==null){
            root=new BTreeNode(key);
            root.position=0;
            return;
        }
        this.add(root, key);
    }

    public void add(BTreeNode node,int key){
        if(node.keys.indexOf(key)!=-1)
            return;
        if(node.keys.size()>=(2*BTree.M-1))
            node=split(node);
        int index=binarySearch(node,key);
        if(node.sons.size()>0){
            if(node.sons.get(index)!=null){
                add(node.sons.get(index),key);
            }else
                node.keys.add(index, key);
        }else{
            node.keys.add(index, key);
        }
    }

    public void delete(int key){
        if(root==null)
            return;
        this.delete(root, key);
    }

    //删除节点
    public void delete(BTreeNode node,int key){
        int index=node.keys.indexOf(key);
        if(index==-1){
            if(node.sons.size()>0){
                index=binarySearch(node,key);
                if(node.father!=null||node.keys.size()<(BTree.M-1)){
                    node=configKeys(node);
                    index=binarySearch(node,key);
                }
                delete(node.sons.get(index),key);

            }
        }else{
            deleteAndCombine(node,index);
        }
    }


    //分裂已经满关键字的节点。当向节点添加关键字的时候，如果此节点已经满关键字，则进行分裂。
    //并且沿途分裂已经满的关键字（即使关键字不需要插入到此节点中)
    public BTreeNode split(BTreeNode node){
        if(node.keys.size()<(2*BTree.M-1))
            return node;
        int n1=BTree.M-1-1;
        int n2=n1+1;
        int n3=2*BTree.M-1-1;
        BTreeNode nodeFather=node.father;
        LinkedList<Integer> newNodesKeys=new LinkedList<Integer>();
        newNodesKeys.addAll(node.keys.subList(n2+1, n3+1));
        BTreeNode newNode=new BTreeNode(newNodesKeys);
        newNode.position=node.position+1;
        List<Integer>lists=new LinkedList<Integer>();
        lists.addAll(node.keys.subList(0, n1+1));
        if(nodeFather==null){
            nodeFather=new BTreeNode();
            nodeFather.keys.add(node.keys.get(n2));
            nodeFather.sons.add(0,node);
            newNode.father=nodeFather;
            nodeFather.sons.add(1,newNode);
            nodeFather.position=0;
            node.father=nodeFather;
            root=nodeFather;
        }else{
            nodeFather.keys.add(node.position, node.keys.get(n2));
            newNode.father=nodeFather;
            nodeFather.sons.add(node.position+1,newNode);
            for(int i=node.position+2;i<=nodeFather.sons.size()-1;i++){
                nodeFather.sons.get(i).position=i;
            }

        }
        if(node.sons.size()>0){
            LinkedList<BTreeNode> newSons=new LinkedList<BTreeNode>();
            LinkedList<BTreeNode> sons=new LinkedList<BTreeNode>();
            newSons.addAll(node.sons.subList(BTree.M, 2*BTree.M));
            for(int i=0;i<=newSons.size()-1;i++){
                newSons.get(i).position=i;
                newSons.get(i).father=newNode;
            }
            sons.addAll(node.sons.subList(0, BTree.M));
            newNode.sons=newSons;
            node.sons.clear();
            node.sons.addAll(sons);

        }
        node.keys.clear();
        node.keys.addAll(lists);
        return split(nodeFather);
    }

    //合并两个节点
    public void combine(BTreeNode node1,BTreeNode node2){
        BTreeNode f=node1.father;
        if(f.sons.size()==2){
            node1.keys.addAll(f.keys);
            node1.keys.addAll(node2.keys);
            f.sons.remove(1);
            node1.father=null;
            root=node1;
        }else{
            node1.keys.add(f.keys.get(node1.position));
            node1.keys.addAll(node2.keys);
            f.keys.remove(node1.position);
            f.sons.remove(node2.position);
        }
        for(int i=node2.position;i<f.sons.size();i++)
            f.sons.get(i).position=i;
        for(int i=0,j=node1.sons.size();i<node2.sons.size();i++,j++){
            node2.sons.get(i).position=j;
            node2.sons.get(i).father=node1;
        }
        node1.sons.addAll(node2.sons);

        configKeys(f);

    }

    //删除关键字，searchLeft搜到比要删除关键字大的最小关键字所在叶子节点，将此关键字和其对换，
    //沉底到叶子节点进行删除，然后还要对叶子节点进行些符合B-tree的调整
    public void deleteAndCombine(BTreeNode node,int keyIndex) {
        if(node.sons.size()>0){
            BTreeNode left=searchLeft(node.sons.get(keyIndex+1));
            node.keys.remove(keyIndex);
            node.keys.add(keyIndex,left.keys.get(0));
            left.keys.remove(0);
            configKeys(left);
        }else{
            node.keys.remove(keyIndex);
            configKeys(node);
        }
    }

    //搜索node子节点中最左结点
    public BTreeNode searchLeft(BTreeNode node){
        if(node.sons.size()>0){
            return searchLeft(node.sons.get(0));
        }else{
            return node;
        }
    }

    /**
     * 避免回溯，从树根向下搜索关键字的过程中，凡是遇到途经的结点，如果该结点的关键字数是t-1，
     * 想办法从其他地方弄关键字过来，使得该结点的关键字数至少为t。
     * 考虑从相邻结点弄，如果相邻结点有的话，经过父结点进行周转。如果没有，
     * 就说明相邻结点的关键字个数也是t-1，这种情况，直接对该结点与其相邻结点进行合并，以满足要求。
     * @param node
     */
    public BTreeNode configKeys(BTreeNode node){
        if(node.keys.size()<=BTree.M-1){
            BTreeNode f=node.father;
            BTreeNode nodeRight=null;
            BTreeNode nodeLeft=null;
            if(f==null)
                return node;
            if(node.position==0)
                nodeRight=f.sons.get(node.position+1);
            else if(node.position==f.keys.size())
                nodeLeft=f.sons.get(node.position-1);
            else{
                nodeLeft=f.sons.get(node.position-1);
                nodeRight=f.sons.get(node.position+1);
            }

            if(nodeRight!=null&&nodeRight.keys.size()>BTree.M-1){
                int temp=f.keys.get(node.position);
                f.keys.remove(node.position);
                f.keys.add(node.position, nodeRight.keys.get(0));
                nodeRight.keys.remove(0);
                node.keys.add(temp);
                if(nodeRight.sons.size()>0){
                    BTreeNode n=nodeRight.sons.get(0);
                    n.position=node.sons.size();
                    n.father=node;
                    node.sons.add(n);
                    nodeRight.sons.remove(0);
                    for(int i=0;i<nodeRight.sons.size();i++)
                        nodeRight.sons.get(i).position=i;
                }
                return node;
            }else if(nodeLeft!=null&&nodeLeft.keys.size()>BTree.M-1){
                int temp=f.keys.get(node.position-1);
                f.keys.remove(node.position-1);
                f.keys.add(node.position-1, nodeLeft.keys.get(nodeLeft.keys.size()-1));
                nodeLeft.keys.remove(nodeLeft.keys.size()-1);
                node.keys.add(0,temp);
                if(nodeLeft.sons.size()>0){
                    BTreeNode n=nodeLeft.sons.get(nodeLeft.sons.size()-1);
                    n.position=0;
                    n.father=node;
                    node.sons.add(0,n);
                    for(int i=1;i<node.sons.size();i++)
                        node.sons.get(i).position=i;
                    nodeLeft.sons.remove(nodeLeft.sons.size()-1);
                }
                return node;
            }else{
                if(nodeLeft!=null){
                    combine(nodeLeft,node);
                    return nodeLeft;
                }else if(nodeRight!=null){
                    combine(node,nodeRight);
                    return node;
                }
            }
        }
        return node;
    }

    //二分查找
    public int binarySearch(BTreeNode node,Integer key){
        int index=0;
        if(node.keys.size()>0){
            int start=0;
            int end=node.keys.size()-1;
            int step=0;
            if(start!=end)
                while((end-start)!=1){
                    step=(end-start)/2;
                    if(node.keys.get(start+step)>key){
                        end=end-step;
                    }else if(node.keys.get(start+step)<key){
                        start=start+step;
                    }else{
                        return start+step;
                    }
                }

            if(key>=node.keys.get(end)){
                index=end+1;
            }else if(key<=node.keys.get(start)){
                index=start;
            }else
                index=end;
        }
        return index;
    }
}

