package com.abc.com.abc.core.datamining.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

/**
  * @author ${user.name}
  */
object App {

  def main(args : Array[String]) {
    val logFile = "D:\\DevN\\i-b-hadoop\\spark-2.1.0-bin-hadoop2.6\\README.md"    /**为你的spark安装目录**/
    val conf=new SparkConf().setAppName("App")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile,2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()

//    println("Lines with a: %s,Lines with b: %s".format(numAs,numBs))
    println("Lines with a: %s,Lines with b: %s".format(1,2))

  }

}