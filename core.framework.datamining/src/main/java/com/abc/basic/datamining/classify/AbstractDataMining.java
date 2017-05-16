package com.abc.basic.datamining.classify;

import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.matrix.DefaultMatrix;

import java.util.TreeMap;

/**
 * 装载数据
 * 执行训练方法
 */
abstract public class AbstractDataMining<K extends Comparable<K>,V> {
    public static final String PATH_NAME="D:\\DevN\\sample-data\\pydatamining\\";
    protected DefaultMatrix dataMatrix;
    protected String[] labels;
    protected ST<K,V> st;
    protected TreeMap<Double, Integer> mapSort;

    public String getLabels(int row){
        return labels[row];
    }

    public void createDataSet(){
        double[][] vals =loadDataSet();
        labels=createLabels();
        dataMatrix=new DefaultMatrix(vals);
    }

    public DefaultMatrix.Shape getShape(){
        return dataMatrix.getShape();
    }

    public ST<K,V> train(double[] inX, int k){
        createDataSet();
        buildClassifyMatrix(inX);
        st=classify(inX, k);
        return st;
    }

    abstract public  double[][] loadDataSet();

    abstract public String[] createLabels();

    abstract public ST<K,V>  classify(double[] inX, int k);

    abstract public void  buildClassifyMatrix(double[] inX);


}
