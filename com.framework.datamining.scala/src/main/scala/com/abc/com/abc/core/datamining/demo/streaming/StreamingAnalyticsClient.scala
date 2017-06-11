package com.abc.com.abc.core.datamining.demo.streaming

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingAnalyticsClient {
  def main(args: Array[String]): Unit = {
    val ssc=new StreamingContext("local[2]","Stream App",Seconds(10))
    val stream=ssc.socketTextStream("localhost",9999)

    //基于原始文本元素生成活动流
    val events=stream.map{record=>
      val event=record.split(",")
      (event(0),event(1),event(2))
    }
    //使用foreachRDD函数对流上的每个RDD应用任意处理函数
    //计算并输出每个批次的状态。因为每个批次都会生成RDD，所以在DStream上调用foreachRDD
    events.foreachRDD{
      (rdd,time)=>
        val numPurchases=rdd.count()
        val uniqueUsers=rdd.map{case (user,_,_)=>user}.distinct().count()
        val totalRevenue=rdd.map{case (_,_,price)=>price.toDouble}.sum()
        val productByPopularity=rdd.map{
          case (user,product,price)=>(product,1)
        }.reduceByKey(_+_).collect().sortBy(-_._2)
        //
        val mostPopular=productByPopularity(0)
        val formatter=new SimpleDateFormat
        val dateStr=formatter.format(new Date(time.milliseconds))
        println(s"Batch start time:\t $dateStr")
        println("Total purchases:\t"+numPurchases)
        println("Unique users:\t"+uniqueUsers)
        println("Most popular product: %s with %d purchaes".format(mostPopular._1,mostPopular._2))
    }

    //开始执行spark上下文
    ssc.start()
    ssc.awaitTermination()
  }
}
