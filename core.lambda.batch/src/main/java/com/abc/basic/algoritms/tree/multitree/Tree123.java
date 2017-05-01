package com.abc.basic.algoritms.tree.multitree;

class DataItem{

    protected final long dData;

    DataItem(long dd){
        this.dData=dd;
    }
    public void displayDataItem(){
        System.out.println("/"+this.dData);
    }
}//DataItem

class Node{
    private static final int ORDER=4;
    private int numItems;
    private Node parent;
    private Node[] childArray=new Node[ORDER];
    private DataItem[] itemArray=new DataItem[ORDER-1];

    //connect child to this node
    public void connectChild(int childNum,Node child){
        this.childArray[childNum]=child;
        if(child!=null){
            child.parent=this;
        }
    }

    //disconnect child to this node
    public Node disconnectChild(int childNum){
        Node child=this.childArray[childNum];
        this.childArray[childNum]=null;
        return child;
    }

    public Node getChild(int childNum){
        return this.childArray[childNum];
    }

    public Node getParent(){
        return this.parent;
    }

    public boolean isLeaf(){
        return (this.childArray[0]==null)?true:false;
    }

    public int getNumItems(){
        return numItems;
    }

    public DataItem getItem(int index){
        return this.itemArray[index];
    }

    public boolean isFull(){
        return (this.numItems==ORDER-1)?true:false;
    }

    /**
     *
     * @param key
     * @return
     */
    public int findItem(long key){
        for(int j=0;j<ORDER-1;j++){
            if(itemArray[j]==null){
                break;
            }else if(itemArray[j].dData==key){
                return j;
            }
        }
        return -1;
    }

    public int insertDataItem(DataItem newItem){
        numItems++;
        long newKey=newItem.dData;
        for(int j=ORDER-2;j>=0;j--){
            if(this.itemArray[j]==null){
                continue;
            }else{
                long itsKey=this.itemArray[j].dData;
                if(newKey<itsKey){//输入的Key小于
                    this.itemArray[j+1]=this.itemArray[j];
                }else{
                    this.itemArray[j+1]=newItem;
                    return j+1;
                }
            }
        }
        this.itemArray[0]=newItem;
        return 0;
    }

    public DataItem removeDataItem(){
        DataItem dataItem=this.itemArray[this.numItems-1];
        this.itemArray[this.numItems-1]=null;
        this.numItems--;
        return dataItem;
    }

    public void displayNode(){
        for(int j=0;j<numItems;j++){
            itemArray[j].displayDataItem();
        }
        System.out.println("/");
    }
}

class TreeMult {

    private Node root=new Node();

    public int find(long key){
        Node currentNode=root;
        int childNumber;
        while(true){
            if((childNumber=currentNode.findItem(key))!=-1){
                return childNumber;
            }else if(currentNode.isLeaf()){
                return -1;
            }else{
                currentNode=getNextItem(currentNode,key);
            }
        }
    }

    public Node getNextItem(Node theNode,long theValue){
        int j;
        int numItem=theNode.getNumItems();
        for(j=0;j<numItem;j++){
            if(theValue<theNode.getItem(j).dData){
                return theNode.getChild(j);
            }
        }
        return theNode.getChild(j);
    }

    public void insert(long dValue){
        Node currentNode=root;
        DataItem tmpItem=new DataItem(dValue);
        while (true){
            if(currentNode.isFull()){
                split(currentNode);
                currentNode=currentNode.getParent();
                currentNode=getNextChild(currentNode,dValue);
            }else if(currentNode.isLeaf()){
                break;
            }else{
                currentNode=getNextChild(currentNode,dValue);
            }
        }
        currentNode.insertDataItem(tmpItem);
    }

    public void split(Node thisNode){
        DataItem itemB,itemC;
        Node parent,child2,child3;
        int itemIndex;

        //
        itemB=thisNode.removeDataItem();
        itemC=thisNode.removeDataItem();
        child2=thisNode.disconnectChild(2);
        child3=thisNode.disconnectChild(3);

        //
        Node newRight=new Node();
        if(thisNode==root){
            root=new Node();
            parent=root;
            root.connectChild(0,thisNode);
        }else{
            parent=thisNode.getParent();
        }
        //
        itemIndex=parent.insertDataItem(itemB);
        int n=parent.getNumItems();
        for(int j=n-1;j>itemIndex;j--){
            Node tmp=parent.disconnectChild(j);
            parent.connectChild(j+1,tmp);
        }
        //
        parent.connectChild(itemIndex+1,newRight);

        //处理newRight
        newRight.insertDataItem(itemC);
        newRight.connectChild(0,child2);
        newRight.connectChild(1,child3);
    }

    public Node getNextChild(Node theNode,long theValue){
        int j;
        int numItems=theNode.getNumItems();
        for(j=0;j<numItems;j++){
            if (theValue<theNode.getItem(j).dData){
                return theNode.getChild(j);
            }
        }
        return theNode.getChild(j);
    }

    public void displayTree(){
        recTree(this.root,0,0);
    }

    private void recTree(Node thisNode,int level,int childNumber){
        System.out.println("level="+level+" childNumber="+childNumber);
        if(thisNode==null){
            return;
        }
        thisNode.displayNode();

        int numItems=thisNode.getNumItems();
        for(int j=0;j<numItems+1;j++){
            Node nextNode=thisNode.getChild(j);
            if(thisNode!=null){
                recTree(nextNode,level+1,j);
            }else{
                return;
            }
        }
    }
}

public class Tree123 {
    public static  void main(String[] args){
        long value;
        TreeMult treeMult=new TreeMult();
        treeMult.insert(10);
        treeMult.insert(80);
        treeMult.insert(30);
        treeMult.insert(40);
        treeMult.displayTree();
    }
}
