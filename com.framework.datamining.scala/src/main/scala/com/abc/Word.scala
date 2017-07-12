package com.abc

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

/**
  * Created by asagaama on 16/02/2017.
  */
object Word {

  def countWords(sc: SparkContext) = {
    // Load our input data
    val input = sc.textFile("/Users/Documents/spark/testscase/test/test.txt")
    // Split it up into words
    val words = input.flatMap(line => line.split(" "))
    // Transform into pairs and count
    val counts = words.map(word => (word, 1)).reduceByKey { case (x, y) => x + y }
    // Save the word count back out to a text file, causing evaluation.
    counts.saveAsTextFile("/Users/Documents/spark/testscase/test/result.txt")
  }

  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("wordCount")
    val sc = new SparkContext(conf)
    countWords(sc)
  }

}
