package com.abc.com.abc.core.datamining.demo.streaming

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by admin on 2017/6/11.
  */
object StreamingExamplesClient {
  def main(args: Array[String]): Unit = {
    val ssc=new StreamingContext("local[2]","Stream App",Seconds(10))
    val stream=ssc.socketTextStream("localhost",9999)
    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
