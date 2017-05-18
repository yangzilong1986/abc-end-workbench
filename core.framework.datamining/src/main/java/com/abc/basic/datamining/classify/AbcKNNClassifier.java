package com.abc.basic.datamining.classify;

import com.abc.basic.algoritms.matrix.DefaultMatrix;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;


public class AbcKNNClassifier extends AbstractDataMining {

    private static final Logger log = LoggerFactory.getLogger(AbcKNNClassifier.class);

    protected TreeMap<Double, Integer> mapSort;

    double[] inX;
    int k;

    public void createDataSet(){
        double[][] vals =loadDataSet();
        labels=createLabels();
        dataMatrix=new DefaultMatrix(vals);
    }

    public  double[][] loadDataSet(){
        double[][] vals = {
                { 1., 1.1},
                {1. ,  1.},
                {0. ,  0. },
                {0.,  0.1}};
        return vals;
    }

    @Override
    public Object[] createLabels(){
        if(labels==null) {
            labels = new String[]{"A", "A", "B", "B"};
        }
        return labels;
    }


    public TreeMap classify(double[] inX, int k) {
        Collection<Integer> mapSortCount= (Collection) mapSort.values();
        Integer[] gg= (Integer[]) mapSortCount.toArray(new Integer[0]);
        TreeMap<String, Integer> classCount=new TreeMap<String, Integer>();
        for(int i=0;i<k;i++){
            int count=0;
            String label= (String) getLabels(gg[i]);
            Integer current=classCount.get(label);
            if(current==null){
                count=1;
            }else {
                count+=current;
            }
            classCount.put(label,count);
        }
        return classCount;
    }

    public void  buildClassifyMatrix(){
        log.info("dataMatrix = " + dataMatrix);
        DefaultMatrix inXMatrix = new DefaultMatrix(inX, DefaultMatrix.Axis.row,getShape().row);//两列

        log.info("inXMatrix = " + inXMatrix);
        DefaultMatrix minusMatrix=inXMatrix.minus(dataMatrix);
        log.info("minusMatrix = " + minusMatrix);

        DefaultMatrix posMatrix=minusMatrix.pow(2);
        log.info("posMatrix = " + posMatrix);

        DefaultMatrix plusMatrix=posMatrix.plus(DefaultMatrix.Axis.col);
        log.info("plusMatrix = " + plusMatrix);

        DefaultMatrix sqrtMatrix =plusMatrix.sqrt();

        log.info("sqrtMatrix = " + sqrtMatrix);

        mapSort=sqrtMatrix.sortVectorByKey(0);
    }

    @Override
    public void readObject(ObjectMapper mapper, String json) throws JsonProcessingException, IOException {
        TreeMap desicTree = mapper.readValue(json, TreeMap.class);
        mapSort=desicTree;
    }


    @Override
    /**
     * 训练结果数据存储
     * @return
     */
    protected String doingTrainResultFileName() {
        return "kNN-tree.txt";
    }
    /**
     * 训练数据文件名称
     * @return
     */
    @Override
    protected String doingTrainDataFileName(){
        return "logistic.txt";
    }

    public TreeMap<String,Object> nativeTrain(){
        createDataSet();
        buildClassifyMatrix();
        TreeMap st=mapSort;
         return st;
    }
    public TreeMap natvieClassify(){

        return classify(inX, k);
    }
    public static void main(String[] args){
        AbcKNNClassifier kNN=new AbcKNNClassifier();
        double[] inX={0.5,0.5};
        kNN.inX=inX;
        kNN.k=1;
        Map k=kNN.classify();
        log.info("训练结果为:"+k);
    }
}
