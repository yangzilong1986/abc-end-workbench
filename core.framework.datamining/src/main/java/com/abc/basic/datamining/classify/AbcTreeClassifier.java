package com.abc.basic.datamining.classify;


import com.abc.basic.algoritms.algs4.col.Bag;
import com.abc.basic.algoritms.algs4.col.SET;
import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.tree.IndexMinPQ;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import com.abc.basic.algoritms.matrix.DefaultMatrix;
import com.abc.basic.algoritms.matrix.DefaultVector;
import org.apache.commons.collections.CollectionUtils;


import org.apache.commons.collections.set.MapBackedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AbcTreeClassifier<K extends Comparable<K>,V> extends AbstractDataMining
{
    private static final Logger log = LoggerFactory.getLogger(AbcTreeClassifier.class);
    ST decisionTree=new ST();
    List<String> classLabels=null;

    public ST<K,V> train(double[] inX, int k){
        createDataSet();
        List dataSet=loadDataFormFile("lenses.txt","\t");

        ST st=new ST();
        classLabels=new ArrayList<>();
        for(String label:this.labels){
            classLabels.add(label);
        }
        createTree(dataSet,classLabels);
        return st;
    }

    public void createTree(List dataSet,List<String> gainLabels){
        List<String> classList=obtainFectList(dataSet,-1);
        int count=countInList(classList,classList.get(0));
        String cl;
        if(count==classList.size()){
            cl= classList.get(0);
            TreeMap<String,Object> subTree=new TreeMap<>();
            decisionTree.put(cl,subTree);
            return;
        }
        List<String> firstVector= (List<String>) dataSet.get(0);
        if(firstVector.size()==1){
            cl= majorityCnt(classList );
            return;
        }

        int bestFeat=chooseBestFeatureToSplit(dataSet);
        if(bestFeat==-1){
            return ;
        }

        String bestFeatLabel=gainLabels.remove(bestFeat);
        TreeMap<String,Object> subTree=new TreeMap<>();
        decisionTree.put(bestFeatLabel,subTree);
        //获取分类值
        Set<String> feactValues=obtainFectSet(dataSet,bestFeat);
        for(String value:feactValues){
            List<String> subLabels=new ArrayList<>();
            subLabels.addAll(gainLabels);
            List retDataSet=splitDataSet(dataSet,bestFeat,value);
            subTree.put(bestFeatLabel,value);
            createTree(retDataSet,subLabels);

            log.info("训练样本为空,c:");
        }

    }

    int countInList(List<String> list,String value){
        Objects.requireNonNull(list);
        Objects.requireNonNull(value);
        int count=0;
        for(String current:list){
            if(current.equals(value)){
                ++count;
            }
        }
        return count;
    }
    String majorityCnt(List classList ){
        Objects.requireNonNull(classList);
        TreeMap<String,Integer> majorTree=new  TreeMap<String,Integer>();
        for(int i=0;i<classList.size();i++){
            String vote= (String) classList.get(i);
            if(!majorTree.containsKey(vote)){
                majorTree.put(vote,0);
            }else{
                majorTree.put(vote,majorTree.get(vote)+1);
            }
        }
        TreeMap<Integer,String>  pq=new TreeMap<Integer,String>();
        for(String vote:majorTree.keySet()){
            pq.put(majorTree.get(vote),vote);
        }
        return pq.lastEntry().getValue();
    }
    //classList = [example[-1] for example in dataSet]
    /**
     * 选择最好的数据划分集
     * @param dataSet
     * @return
     */
    protected int chooseBestFeatureToSplit(List dataSet){
        int bastFeature=-1;
        double bestInfoGain=0.0;
        Objects.requireNonNull(dataSet);

        List<String> firstVector= (List<String>) dataSet.get(0);
        Objects.requireNonNull(firstVector);
        int numFeatures=firstVector.size()-1;
        double bestEntropy=calcShannonEnt(dataSet);
        Set<String> setVector=new TreeSet<String>();
        for(int i=0;i<numFeatures;i++){
            //获取数据
            Set<String> featList=obtainFectSet(dataSet,i);
            double newEntropy=0.0;
//            log.info("本次样本数据为:"+featList);
            for(String value:featList ){
                //分裂数据
                List subDataList=splitDataSet(dataSet,i,value);
                double prob=(double)subDataList.size()/(double)dataSet.size();
                newEntropy+=prob*calcShannonEnt(subDataList);

            }
            double infoGain=bestEntropy-newEntropy;
            log.info("本次训练数据的信息增量为:"+infoGain);
            if(infoGain>bestInfoGain){
                bestInfoGain=infoGain;
                bastFeature=i;
            }
        }
        log.info("最优划分数据为:"+bastFeature);
        return bastFeature;
    }

    /**
     * 在List的结构中获取Set
     * @param dataSet
     * @param axis
     * @return
     */
    public Set<String> obtainFectSet(List dataSet,int axis){
        Objects.requireNonNull(dataSet);
        if(CollectionUtils.isEmpty(dataSet)){
            log.info("训练样本为空,axis:"+axis);
        }
        Set<String> setVector=new TreeSet<String>();
        for(int i=0;i<dataSet.size();i++){
            //获取数据
            List<String> fectVector= (List<String>)dataSet.get(i);
            String vector=null;
            try {
                vector = fectVector.get(axis);
                setVector.add(vector);
            }catch (NullPointerException e){
                log.info("dataSet"+dataSet);
                log.info("axis:"+axis);
            }
        }
        return setVector;
    }

    /**
     * 在List的结构中获取Set
     * @param dataSet
     * @param axis
     * @return
     */
    public List<String> obtainFectList(List dataSet,int axis){
        Objects.requireNonNull(dataSet);
        CollectionUtils.isNotEmpty(dataSet);
        List<String> setVector=new LinkedList<String>();
        for(int i=0;i<dataSet.size();i++){
            //获取数据
            List<String> fectVector= (List<String>)dataSet.get(i);
            if(axis==-1) {
                String vector = fectVector.get(fectVector.size()-1);
                setVector.add(vector);
            }else{
                String vector = fectVector.get(axis);
                setVector.add(vector);
            }
        }
        return setVector;
    }

    boolean isNotEqVector(List<String> first,List<String> sec){
        Objects.requireNonNull(first);
        Objects.requireNonNull(sec);
        if(first.size()==sec.size()){
            return false;
        }
        return true;
    }
    /**
     * 计算给定数据集的香农熵
     * @param dataSet
     * @return
     */
    protected double calcShannonEnt(List dataSet){
        double shannonEnt=0.0;
        TreeMap<String,Integer> labelCount=new TreeMap<>();
        Objects.requireNonNull(dataSet);
        if(CollectionUtils.isEmpty(dataSet)){
            log.info("训练样本为空");
        }
        List<String> firstVector= (List<String>) dataSet.get(0);
        Objects.requireNonNull(firstVector);
        for(int i=0;i<dataSet.size();i++){
            List<String> featVec=(List<String>) dataSet.get(i);
            if(isNotEqVector(firstVector,featVec)){
                log.info("本样本数据和前一条数据长度不一致");
                continue;
            }
            String currentLabel=featVec.get(featVec.size()-1);
            if(!labelCount.containsKey(currentLabel)){
                labelCount.put(currentLabel,0);
            }
            labelCount.put(currentLabel,labelCount.get(currentLabel)+1);

        }
        //计算香农值
        double numEntries=dataSet.size();
        for(String label:labelCount.keySet()){
            double prob=(double)labelCount.get(label)/numEntries;
            // log((double)N)/log((double)2)
            shannonEnt-=prob*Math.log(prob)/Math.log((double)2);
        }
        log.info("计算的香农熵值为:"+shannonEnt);
        return shannonEnt;
    }

    protected List splitDataSet(List dataSet,int axis,String value){
        Objects.requireNonNull(dataSet);
        if(CollectionUtils.isEmpty(dataSet)){
            log.info("分裂时数据集不能为空"+"分裂值为");
        }
        Objects.requireNonNull(value);
        List<String> firstVector= (List<String>) dataSet.get(0);
        List currentDataSet=new ArrayList<>();
        for(int i=0;i<dataSet.size();i++){
            List<String> featVec=(List<String>) dataSet.get(i);

            if(isNotEqVector(firstVector,featVec)){
                log.info("本样本数据和前一条数据长度不一致");
                continue;
            }
            if(featVec.get(axis).equals(value)){
                List<String> currentVec=new ArrayList<String>();
                List<String> subLeft=featVec.subList(0,axis);
                List<String> subRight=featVec.subList(axis+1,featVec.size());
                currentVec.addAll(subLeft);
                currentVec.addAll(subRight);
                currentDataSet.add(currentVec);
                log.info("分裂后的一列数据为"+currentVec+" 分裂值为："+value);
            }
        }

        return currentDataSet;
    }
    /**
     *
     * @param fileName
     * @param regex
     * @return
     */
    public List loadDataFormFile(String fileName,String regex ){
        log.info("装载文件名称为:"+fileName);
        In streams=new In(PATH_NAME+fileName);
        //在每个流中读入一个数据，形成index/key
        String[] lines=streams.readAllLines();
        Objects.requireNonNull(lines);
        ST<String,String> rawDataSet=new ST<>();
        List dataSet=new ArrayList<>();
        int col=-1;
        for(String line:lines){
            String[] data=line.split(regex);
            if(col==-1){
                col=data.length;
            }
            if(col!=data.length){//忽略长度不一致的值
                log.info("本样本数据和前一条数据长度不一致");
                continue;
            }
//            TreeMap<Integer,String> item=new TreeMap<>();
//
//            for(int i=0;i<data.length;i++) {
//                item.put(i,data[i]);
//            }
//            dataSet.add(item);
           List<String> item=new ArrayList<>();

            for(int i=0;i<data.length;i++) {
                item.add(data[i]);
            }
            dataSet.add(item);

        }
        DefaultMatrix.Shape shape=new DefaultMatrix.Shape(dataSet.size(),col);
        streams.close();
        log.info("装载数据大小为:"+shape);
        return dataSet;
    }

    public void createDataSet(){
        labels=createLabels();
    }

    @Override
    public double[][] loadDataSet() {
        List dataSet=loadDataFormFile("lenses.txt","\t");
        return new double[0][];
    }

    public  double[][] autoNorm(List data){
        Objects.requireNonNull(data);
        int row=data.size();
        Bag<String> bag= (Bag<String>) data.get(0);
        int col=bag.size();
        DefaultMatrix.Shape shape=new DefaultMatrix.Shape(row,col);
        double[][] dataMatrix=new double[row][col];

        return dataMatrix;
    }

    @Override
    public String[] createLabels() {
        //labels=['age' , 'presscript' , 'astigmatic', 'tearRate']
        return new String[]{"age","presscript","astigmatic","tearRate"};
    }

    @Override
    public ST classify(double[] inX, int k) {
        return null;
    }

    @Override
    public void buildClassifyMatrix(double[] inX) {

    }

    public static void main(String[] args){
        /**
        bestFeatLabel="test"
        myTree = {bestFeatLabel:{}}
        myTree[bestFeatLabel]["testkey"] ="testvalue"
        {'test': {'testkey': 'testvalue'}}
         **/
        TreeMap<String,Object> sub=new TreeMap<String,Object>();
        sub.put("testkey","testvalue");

        TreeMap<String,Object> root=new TreeMap<String,Object>();
        root.put("test",sub);

        AbcTreeClassifier<String,String> kNN=new AbcTreeClassifier();
        double[] inX={0.5,0.5};
        ST st=kNN.train(inX,1);
        log.info("train result = " + st);
    }
}
