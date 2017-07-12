package com.abc.com.abc.core.datamining.demo.streaming

import breeze.linalg.DenseVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 创建流回归模型
  * 一个简单的线性回归计算并出每个批次的预测值
  */
object StreamingModelClient {

  def main(args: Array[String]): Unit = {
    val ssc = new StreamingContext("local[2]", "Stream App", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)
    //建立大量的特征来匹配输入的流数据记录的特征。
    //创建一个零向量来作为流回归模型的初始权值向量。
    //最后，选择迭代次数和步长
    val NumFeatrues=100
    val zeroVector=DenseVector.zeros[Double](NumFeatrues)
    val model=new StreamingLinearRegressionWithSGD()
        .setInitialWeights(Vectors.dense(zeroVector.data))
        .setNumIterations(1)
        .setStepSize(0.01)

    //使用map把DStream中字符串表示的每个记录转换为LabelPoint实例，包括目标值和特征向量
    //创建一个标签点的流
    val labeledStream=stream.map{
      event=>
        val split=event.split("\t")
        val y=split(0).toDouble
        val features = split(1).split(",").map(_.toDouble)
        //目标值和特征向量
        LabeledPoint(label = y,features=Vectors.dense(features))
    }
    //通知模型在转换后的DStream上做训练，以及测试并输出DStream每一批数据前几个元素的预算值
    model.trainOn(labeledStream)
    model.predictOnValues(labeledStream.map(lp => (lp.label, lp.features))).print()
    ssc.start()
    ssc.awaitTermination()
  }


}
