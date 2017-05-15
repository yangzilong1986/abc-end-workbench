package com.abc.basic.datamining.classify.knn;

import com.abc.basic.algoritms.algs4.col.ST;
import com.abc.basic.algoritms.algs4.utils.StdOut;
import com.abc.basic.algoritms.matrix.DefaultMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class AbcKNN extends AbstractDataMining{
    private static final Logger log = LoggerFactory.getLogger(AbcKNN.class);
    @Override
    public  double[][] loadDataSet(){
        double[][] vals = {
                { 1., 1.1},
                {1. ,  1.},
                {0. ,  0. },
                {0.,  0.1}};
        return vals;
    }

    @Override
    public String[] createLabels(){
        return new String[]{"A","A","B","B"};
    }

    @Override
    public ST classify(double[] inX, int k) {
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

        TreeMap<Double, Integer> mapSort=sqrtMatrix.sortVectorByKey(0);
        Collection<Integer> mapSortCount= (Collection) mapSort.values();
        Integer[] gg= (Integer[]) mapSortCount.toArray(new Integer[0]);
        ST<String, Integer> classCount=new ST<String, Integer>();
        for(int i=0;i<k;i++){
            int count=0;
            String label=getLabels(gg[i]);
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

    public static void main(String[] args){
        AbstractDataMining kNN=new AbcKNN();
        double[] inX={0.5,0.5};
        ST st=kNN.train(inX,1);
        log.info("train result = " + st);
    }
}
