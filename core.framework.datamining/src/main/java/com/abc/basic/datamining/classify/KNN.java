package com.abc.basic.datamining.classify;

import Jama.Matrix;

/**
 * 分类：最近邻分类器
 */
public class KNN {

    Matrix dataSet;
    String[] labels;

    public void createDataSet(){
       double[][] vals = {{ 1., 1.1},{1. ,  1.},{0. ,  0. },{0.,  0.1}};
        dataSet=new Matrix(vals);
    }

    public void createLabels(){
        labels=new String[]{"A","A","B","B"};
    }

    public void classify(double[] inX, int k){
        int row=dataSet.getRowDimension();
        //print(a.sum(axis=1)) # 对列方向求和
         Matrix datTile=new Matrix(inX, Matrix.Axis.row,row);
        //差值
        double[] f=dataSet.getArray()[0];
        Matrix subValue=datTile.minus(dataSet).pow(2);


    }

    public static void main(String[] args){
        testMatrix();
        KNN kNN=new KNN();
        kNN.createDataSet();
        kNN.createLabels();
        double[] inX={0,0};
        kNN.classify(inX,1);
    }

    public static void testMatrix(){
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.},{11.,12.,13.},{14.,15.,16.}};

        double[] datas = {1, 2};
        Matrix ad = new Matrix(datas,2);

        Matrix c =new Matrix(1,4,1);
        double[] dd={1,1,1,1,1,1};
        Matrix d =new Matrix(dd,1);

        Matrix A = new Matrix(vals);
        int col=A.getColumnDimension();//n
        int row=A.getRowDimension();//m
        A.print(2,0);

        Matrix b = Matrix.random(5,1);

        Matrix m=A.getMatrix(0,row-1,0,0);

        Matrix x = A.solve(b);
        Matrix r = A.times(x).minus(b);
        double rnorm = r.normInf();
    }
}
