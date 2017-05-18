package com.abc.basic.datamining.classify;


import com.abc.basic.algoritms.algs4.col.Bag;
import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.search.BinarySearch;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.matrix.DefaultMatrix;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;


import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class AbcTreeClassifier<K extends Comparable<K>,V> extends AbstractDataMining
{
    private static final Logger log = LoggerFactory.getLogger(AbcTreeClassifier.class);
    protected  List<String> classLabels=null;


    protected List<String> testData=null;

    public void setTestData(List testData){
        this.testData=testData;
    }
    public Map natvieClassify(){
        List test=testData;
        return (Map) classify((TreeMap<String, Object>) st);
    }
    public Map classify(Map<String,Object> inputTree){
        for(String firstStr:inputTree.keySet()){
            Map<String,Object> secondDict = (Map<String, Object>) inputTree.get(firstStr);
            int featIndex = indexClassLabels(firstStr);
            String key = testData.get(featIndex);
            Map valueOfFeat = (Map) secondDict.get(key);
            if(log.isInfoEnabled()){
                log.info("关键字为:"+firstStr+" 在分类结果的索引为:"+featIndex+" 在测试数据中:"+key+
                        " 检查结果为"+valueOfFeat);
            }
            valueOfFeat.entrySet();
            Collection values= (Collection) valueOfFeat.values();
            Object[] valueOfFeatArray=values.toArray(new Object[0]);
            if(values!=null&&!values.isEmpty()){
                for(int i=0;i<values.size();i++){
                    Object value=valueOfFeatArray[i];
                    if(value instanceof String){
                        return valueOfFeat;
                    }
                }
            }
            if(valueOfFeat instanceof Map){
                return classify((Map<String,Object>)valueOfFeat);
            }
        }
        return null;
    }

    protected int indexClassLabels(String label){
        for(int i=0;i<labels.length;i++){
            if(label.equals(labels[i])){
                return i;
            }
        }
        return  -1;
    }


    public  TreeMap<String,Object> nativeTrain(){
        TreeMap<String,Object> d=createTree(dataSet,classLabels);
        return d;
    }

    public  void readObject(ObjectMapper mapper,String json)throws JsonProcessingException,IOException{
        TypeReference typeReference=new TypeReference<TreeMap<String,TreeMap<String,TreeMap>>>(){
            public Type getType() {
                return this._type;
            }
        };
        TreeMap desicTree = mapper.readValue(json, typeReference);
        st=desicTree;
    }

    public TreeMap<String,Object> createTree(List dataSet,List<String> gainLabels){
        List<String> classList=obtainFectList(dataSet,-1);
        int count=countInList(classList,classList.get(0));
        if(count==classList.size()){
            String cl= classList.get(0);
            TreeMap<String,Object> leafTree=new TreeMap<>();
            leafTree.put(cl,"leaf"+cl);
            return leafTree;
        }
        List<String> firstVector= (List<String>) dataSet.get(0);
        if(firstVector.size()==1){
            String cl= classList.get(0);
            TreeMap<String,Object> leafTree=new TreeMap<>();
            cl= majorityCnt(classList );
            leafTree.put(cl,"leaf"+cl);
            return leafTree;
        }

        int bestFeat=chooseBestFeatureToSplit(dataSet);
        if(bestFeat==-1){
            return null;
        }

        String bestFeatLabel=gainLabels.remove(bestFeat);
        TreeMap<String,Object> rootTree=new TreeMap<>();
        TreeMap<String,Object> curruentTree=new TreeMap<>();
        rootTree.put(bestFeatLabel,curruentTree);
        //获取分类值
        Set<String> feactValues=obtainFectSet(dataSet,bestFeat);
        for(String value:feactValues){//
            List<String> subLabels=new ArrayList<>();
            subLabels.addAll(gainLabels);
            List retDataSet=splitDataSet(dataSet,bestFeat,value);

            TreeMap<String,Object> subTree=createTree(retDataSet,subLabels);
            curruentTree.put(value,subTree);
            if(log.isInfoEnabled()) {
                log.info("分类决策树为," + subTree);
            }
        }
        return rootTree;
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
            if(log.isDebugEnabled()) {
                log.info("本次样本数据为:" + featList);
            }
            for(String value:featList ){
                //分裂数据
                List subDataList=splitDataSet(dataSet,i,value);
                double prob=(double)subDataList.size()/(double)dataSet.size();
                //计算香农熵
                newEntropy+=prob*calcShannonEnt(subDataList);

            }
            double infoGain=bestEntropy-newEntropy;
            if(log.isDebugEnabled()) {
                log.debug("训练数据分类为:" + dataSet);
                log.debug("本次训练数据的信息增量为:" + infoGain);
            }
            if(infoGain>bestInfoGain){
                bestInfoGain=infoGain;
                bastFeature=i;
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("最优划分数据为:" + bastFeature);
        }
        return bastFeature;
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
                log.warn("本样本数据和前一条数据长度不一致");
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
        if(log.isInfoEnabled()) {
            log.info("训练数据:" + dataSet);
            log.info("计算的香农熵值为:" + shannonEnt);
        }
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
                log.warn("本样本数据和前一条数据长度不一致");
                continue;
            }
            if(featVec.get(axis).equals(value)){
                List<String> currentVec=new ArrayList<String>();
                List<String> subLeft=featVec.subList(0,axis);
                List<String> subRight=featVec.subList(axis+1,featVec.size());
                currentVec.addAll(subLeft);
                currentVec.addAll(subRight);
                currentDataSet.add(currentVec);
            }
        }

        return currentDataSet;
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

    public void createDataSet(){
        labels=createLabels();
        classLabels=new ArrayList<>();
        for(String label:this.labels){
            classLabels.add(label);
        }
        dataSet=loadDataFormFile("\t");
    }

    @Override
    public String[] createLabels() {
        if(labels==null) {
            labels =new String[]{"age", "presscript", "astigmatic", "tearRate"};
        }
        return labels;
    }

    /**
     * 训练数据文件名称
     * @return
     */
    @Override
    protected String setStoreTrainData(){
        return PATH_NAME+"lenses.txt";
    }

    /**
     * 训练结果数据存储
     * @return
     */
    protected String setStoreTrainResultName(){
        return PATH_NAME+"lenses-desc-tree.txt";
    }
}
