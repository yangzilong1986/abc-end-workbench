package com.abc.basic.datamining.classify;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.matrix.DefaultMatrix;
import com.abc.basic.algoritms.matrix.Matrix;
import com.abc.basic.algoritms.matrix.Vector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 逻辑回归分类
 */
public class AbcLogisticRegresClassifer <TainLabel,TainResult>  extends AbstractDataMining {
    private static final Logger log = LoggerFactory.getLogger(AbcLogisticRegresClassifer.class);
    @Override
    public void readObject(ObjectMapper mapper, String json) throws JsonProcessingException, IOException {

    }

    @Override
    public Object nativeTrain() {

        return gradAscent();
    }

    /**
     * 梯度上升计算
     * @return
     */
    public Matrix gradAscent(){

        Integer[] tmpLabels=new Integer[labels.length];
        if (this.labels[0] instanceof Integer){
           for(int i=0;i<labels.length;i++){
               tmpLabels[i]=(Integer)labels[i];
           }
        }
        //训练数据矩阵
        Matrix dataMatrix =listDoubleToMatrix(this.dataSet);
        //分类矩阵
        Matrix labelMat=new Matrix(tmpLabels,1).transposeC();
        double alpha = 0.001;
        int maxCycles = 500;
        Matrix.Shape dataShape=dataMatrix.getShape();
        Matrix weights=new Matrix(dataShape.col,1,1);
        Matrix dataTranpose=dataMatrix.transpose();
        for(int k=0;k<maxCycles;k++){
            //sigmoidParam=dataMatrix*weights
            Matrix sigmoid=dataMatrix.times(weights);
            Matrix h=handelSigmoid(sigmoid);
            //error = (labelMat - h) #vector subtraction
            Matrix error=labelMat.minus(h);
            // weights = weights + alpha *dataTranpose * error #matrix mult
            Matrix tmp=dataTranpose.times(alpha).times(error);
            weights=weights.plusThat(tmp);
        }
        return weights;
    }

//    def sigmoid(inX):
//            return 1.0/(1+exp(-inX))
    public Matrix handelSigmoid(Matrix sigmoid){
        Matrix sigmoidResult=sigmoid.negate().exp().add(1L).divideThis(1.0);

        return sigmoid;
    }
    protected  Matrix listDoubleToMatrix(List<List> dataSet){
        if(CollectionUtils.isEmpty(dataSet)){
            log.warn("训练数据不存在");
            return null;
        }

        List<Double> colList=dataSet.get(0);
        if(CollectionUtils.isEmpty(colList)){
            log.warn("行数据不存在");
            return null;
        }
        int col=colList.size();

        Matrix matrix=new Matrix(col);
        for(List rowData:dataSet ){
            Vector vector=new Vector(col);
            for(int i=0;i<rowData.size();i++){
                vector.put(i,(Double)rowData.get(i));
            }
            matrix.addVector(vector);
        }
        if(log.isDebugEnabled()){
            log.debug("训练数据形成的矩阵为:");
            log.debug(matrix.toString());
        }
        return matrix;
    }
    @Override
    public Map natvieClassify() {
        return null;
    }

    /**
     * 训练数据文件名称
     * @return
     */
    @Override
    protected String doingTrainDataFileName(){
        return "ch05\\testSet.txt";
    }
    /**
     * 训练结果数据存储
     * @return
     */
    protected String doingTrainResultFileName() {
        return "logistic-train-result.txt";
    }

    public TainLabel[] createLabels(){
        return null;
    }

    /**
     * 装载数据
     * @param regex
     * @return
     */
    public List loadDataFormFile(String regex ){
        if(isNotExistFile(this.setStoreTrainData())){
            log.warn("训练数据不存在");
            return null;
        }
        In streams=new In(this.setStoreTrainData());
        if(CollectionUtils.isNotEmpty(dataSet)){
            return dataSet;
        }
        dataSet=new ArrayList<List>();
        int col=0;
        List<Double> list;
        List logisLabels=new ArrayList();
        while(streams.hasNextLine()){
            String[] line=streams.readLine().split(regex);
            if(col==0) {
                col = line.length;
            }
            if(col!=line.length){
                if(log.isInfoEnabled()) {
                    log.info("读入的数据列不一致，忽略此行:" + line);
                }
                continue;
            }
            if(log.isDebugEnabled()){
                log.debug("读入数据为：line:"+line);
            }
            //最后一列解析为分类
            list=new ArrayList<Double>();
            list.add(1d);
            for(int i=0;i<line.length-1;i++){
                list.add(Double.parseDouble(line[i]));

            }
            //一行数据
            dataSet.add(list);
            logisLabels.add(Integer.parseInt(line[line.length-1]));
        }

        //在每个流中读入一个数据，形成index/key
        streams.close();
        this.labels=logisLabels.toArray();
        return dataSet;
    }

}
