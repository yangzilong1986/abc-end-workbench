package com.abc.basic.datamining.classify;

import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.Out;
import com.abc.basic.algoritms.matrix.DefaultMatrix;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 装载数据
 * 执行训练方法
 */
abstract public class AbstractDataMining<K extends Comparable<K>,V> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDataMining.class);

    public static final String PATH_NAME="D:\\DevN\\sample-data\\pydatamining\\";

    protected DefaultMatrix dataMatrix;
    protected String[] labels;
    protected Object st;//训练结果集

    public String getLabels(int row){
        return labels[row];
    }

    public DefaultMatrix.Shape getShape(){
        return dataMatrix.getShape();
    }

    /**
     * 训练数据模型
     * @return
     */
    final public void train(){
        //装载数据
        createDataSet();
        //训练数据模型
        TreeMap<String,Object> d=nativeTrain();
        //存储训练结果
        if(d!=null) {
            storeTrainResult(d);
        }
    }

    final public Map classify(){
        createLabels();
        loadTrainResult();
        return natvieClassify();
    }

    protected void storeTrainResult(Object tain){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(tain);
            Out out=new Out(setStoreTrainResultName());
            out.println(json);
            out.close();
        } catch (JsonProcessingException e) {
            log.error("train result desicTree= " + e.getMessage());
        }catch (IOException e) {
            log.error("train result desicTree= " + e.getMessage());
        }
    }

    protected void loadTrainResult(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            In streams=new In(setStoreTrainResultName());
            String json =streams.readLine();
            readObject(mapper,json);
        } catch (JsonProcessingException e) {
            log.error("train result " + e.getMessage());
        }catch (IOException e) {
            log.error("train result " + e.getMessage());
        }
    }

    abstract public  void readObject(ObjectMapper mapper,String json)throws JsonProcessingException,IOException;

    abstract protected String setStoreTrainData();

    abstract protected String setStoreTrainResultName();

    abstract public  TreeMap nativeTrain();

    abstract public String[] createLabels();

    abstract public void createDataSet();

    abstract public Map natvieClassify();

    ///////////////////////////////////////////////////////////////
    /**
     * 在List的结构中获取Set
     * @param dataSet
     * @param axis
     * @return
     */
    public Set<String> obtainFectSet(List dataSet, int axis){
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
                log.error("dataSet"+e.getMessage());
                log.error("axis:"+axis);
                log.error("dataSet"+dataSet);
                log.error("axis:"+axis);
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

    public boolean isNotEqVector(List<String> first,List<String> sec){
        Objects.requireNonNull(first);
        Objects.requireNonNull(sec);
        if(first.size()==sec.size()){
            return false;
        }
        return true;
    }
}
