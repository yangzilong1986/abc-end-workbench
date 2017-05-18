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
abstract public class AbstractDataMining<TainLabel,TainResult> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDataMining.class);

    public static final String PATH_NAME="D:\\DevN\\sample-data\\pydatamining\\";

    protected DefaultMatrix dataMatrix;

    protected TainLabel[] labels;

    protected TainResult st;//训练结果集

    //训练数据
    protected List dataSet=null;
    //训练数据文件名称
    protected String trainDataFileName;

    //训练结果文件名称
    protected String trainResultFileName;

    public boolean isSameWidth() {
        return isSameWidth;
    }

    public void setSameWidth(boolean sameWidth) {
        isSameWidth = sameWidth;
    }

    //数据文件每行不能宽时，是否忽略
    protected boolean isSameWidth=true;

    public  void setClassifyLabels(TainLabel[] classLabels){
        this.labels=classLabels;
    }

    /**
     * 训练数据文件名称
     * @return
     */
    public void initTrainDataFileName(String trainDataFileName){
        this.trainDataFileName=trainDataFileName;
    }

    /**
     * 训练结果数据存储文件名称
     * @return
     */
    public void initTrainResultFileName(String fileName){
        this.trainResultFileName=fileName;
    }

    public TainLabel getLabels(int row){
        return labels[row];
    }

    public DefaultMatrix.Shape getShape(){
        return dataMatrix.getShape();
    }

    public void buildDataSet(List dataSet){
        this.dataSet=dataSet;
    }

    /**
     * 训练数据模型
     * @return
     */
    final public void train(){
        //兼容开发设置
        if(trainDataFileName==null||trainResultFileName==null) {
            trainDataFileName=doingTrainDataFileName();
            trainResultFileName=doingTrainResultFileName();
        }
        //装载分类信息
        createLabels();
        //装载数据
        createDataSet();
        //训练数据模型
        TainResult d=nativeTrain();
        //存储训练结果
        if(d!=null) {
            storeTrainResult(d);
        }
    }

    final public Map classify(){
        //兼容开发设置
        if(trainDataFileName==null||trainResultFileName==null) {
            trainDataFileName=doingTrainDataFileName();
            trainResultFileName=doingTrainResultFileName();
        }
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
        if(isNotExistFile(this.setStoreTrainData())){
            log.warn("训练结果不存在");
            return ;
        }
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

    /**
     * 训练数据文件名称
     * @return
     */
    final String setStoreTrainData(){
        return PATH_NAME+trainDataFileName;
    }

    /**
     * 训练结果数据存储
     * @return
     */
    final String setStoreTrainResultName(){
        return PATH_NAME+trainResultFileName;
    }



    public void createDataSet(){
        if(CollectionUtils.isEmpty(dataSet)) {
            dataSet = loadDataFormFile("\t");
        }else{
            dataSet.addAll(loadDataFormFile("\t"));
        }
    }

    public boolean isNotExistFile(String fileName){
        try {
            In streams = new In(fileName);
            return false;
        }catch (IllegalArgumentException e){
            log.error("词源文件不存在");
            return true;
        }
    }
    /**
     * @param regex
     * @return
     */
    public List loadDataFormFile(String regex ){
        if(isNotExistFile(this.setStoreTrainData())){
            log.warn("训练数据不存在");
            return null;
        }
        In streams=new In(this.setStoreTrainData());
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
            if(isSameWidth&&col!=data.length){//忽略长度不一致的值
                log.warn("本样本数据和前一条数据长度不一致");
                continue;
            }
            List<String> item=new ArrayList<>();

            for(int i=0;i<data.length;i++) {
                item.add(data[i]);
            }
            dataSet.add(item);

        }
        DefaultMatrix.Shape shape=new DefaultMatrix.Shape(dataSet.size(),col);
        streams.close();
        if(log.isInfoEnabled()) {
            log.info("装载数据大小为:" + shape);
        }
        return dataSet;
    }

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

    abstract public  void readObject(ObjectMapper mapper,String json)throws JsonProcessingException,IOException;

    abstract public  TainResult nativeTrain();

    abstract public Map natvieClassify();

    abstract public TainLabel[] createLabels();

    abstract  protected String doingTrainDataFileName();

    abstract  protected String doingTrainResultFileName();
}
