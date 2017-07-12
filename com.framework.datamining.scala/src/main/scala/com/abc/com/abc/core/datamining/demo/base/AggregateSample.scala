package com.abc.com.abc.core.datamining.demo.base

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object AggregateSample {
  def seq(a:Int,b:Int):Int={
    println("Seq:"+a+ " : " +b)
    math.min(a,b)
  }

  def comb(a:Int,b:Int):Int={
    println("Comb:"+a+ " : " +b)
    a+b
  }

  def main(args : Array[String]): Unit = {
    //-Dspark.master=local
    val conf=new SparkConf().setAppName("AggregateSample")
    val sc = new SparkContext(conf)
//    mainAggregate(sc)
//    mainGroupBy(sc)
//    mainReduceByKey(sc)
//    mainAggregateByKey(sc)
//    mainCombineByKey(sc)
//    mainTreeAggregate(sc)
//    mainAggregateByKey2(sc)
//    mainAggregateByKey3(sc)

//    mainAggregateTuple(sc)
//    mainAggregateByKeyTuple(sc)
//    mainAggregateByKey(sc)
//    mainAggregateByKey_2(sc)
//    mainAggregate(sc)
    //mainAggregateTuple(sc)
    mainTreeAggregate(sc)
  }

  //RDD0
//  Seq-Key-u:	0 : 0.0	Seq-Value-t:	1 : 11.0
//  Seq-Key-u:	1 : 11.0	Seq-Value-t:	1 : 2.0
//  Seq-Key-u:	2 : 13.0	Seq-Value-t:	1 : 3.0
//  Seq-Key-u:	3 : 16.0	Seq-Value-t:	2 : 3.0
  //RDD1
//  Seq-Key-u:	0 : 0.0	Seq-Value-t:	2 : 2.0
//  Seq-Key-u:	2 : 2.0	Seq-Value-t:	3 : 1.0
//  Seq-Key-u:	5 : 3.0	Seq-Value-t:	3 : 2.0
//  Seq-Key-u:	8 : 5.0	Seq-Value-t:	4 : 1.0
//  Seq-Key-u:	12 : 6.0	Seq-Value-t:	4 : 4.0

//  Comb-U-1:	5 : 19.0
//  Comb:U-2:	16 : 10.0
//  (21,29.0)

  def seqOpUion(u:(Int,Double),t:(Int,Double)):(Int,Double)={
    //描述如何将v合并到u，此方法依次遍历每个key，在遍历中key按照逻辑返回
    //u和v均是k-v中的v，逐一遍历RDD，当遍历的key相同时，默认为同一分组，此时下一个u,v的u为本次的返回结果
    // 分组后，不是key时，按照u为zeroValue，v为遍历的k-v中的v。
    println("Seq-Key-u:\t"+u._1+ " : " +u._2+"\tSeq-Value-t:\t"+t._1+ " : " +t._2)
//    (u._1+t._1,u._2+t._2)
//    (u._1,u._2+t._2)
    (t._1,u._2+t._2)
  }

  def combOpUion(u1:(Int,Double),u2:(Int,Double)):(Int,Double)={
    println("Comb-U-1:\t"+u1._1+ " : " +u1._2)
    println("Comb:U-2:\t"+u2._1+ " : " +u2._2)
    (u1._1+u2._1,u1._2+u2._2)
  }
  def mainTreeAggregate(sc :SparkContext): Unit = {
    //-Dspark.master=local
    //加减是对(K,V)中的V运算
    val z = sc.parallelize(List(
      (1, 11.0), (1, 2.0), (1, 3.0), (2, 3.0), //一组
      (2, 2.0),
      (3, 1.0), (3, 2.0),
      (4, 1.0), (4, 4.0)), 2)
    //    val z=sc.parallelize(List((1,3),(1,2),(1,4),(2,3)))
    val agg = z.treeAggregate((0,0.0))(
      seqOpUion,
      combOpUion,
      //seqOp = ((u,t)=>(u._1+t._1,u._2+t._2)),//Key相加，对Key进行操作
      // combOp = (u1,u2)=>(u1._1+u2._1,u1._2+u2._2),//Value操作
      depth = 2
    )
    println(agg)
//    (21,29.0)

//    val result=agg .collect()
//    z.groupBy((x:Int,y:Int)=>x)
//    result.foreach(x=>println("CombineByKey Result: " + x))
  }

  def mainAggregateTuple(sc :SparkContext): Unit = {
    val z = sc.parallelize(List((1, 11.0), (1, 2.0), (1, 3.0), (2, 3.0), (2, 2.0), (3, 1.0), (3, 2.0), (4, 1.0), (4, 4.0)), 3)
    //    val z=sc.parallelize(List((1,3),(1,2),(1,4),(2,3)))
    val agg = z.aggregate((0,0.0))(
      seqOp = ((u,t)=>(u._1+t._1,u._2+t._2)),//
      combOp = (u1,u2)=>(u1._1+u2._1,u1._2+u2._2)//
      //depth = 2
    )//(21,29.0)
    println(agg)
  }

//  def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) => U,
//                                                                          combOp: (U, U) => U):



  def combstr(a: String, b: String): String = {
    println("comb: " + a + "\t " + b)
    a + b
  }
  //合并在同一个partition中的值， a的数据类型为zeroValue的数据类型，b的数据类型为原value的数据类型
  def seqstr(a: String, b: Int): String = {
    println("seq: " + a + "\t " + b)
    a + b
  }

  /**
     (1,3)
     (1,2)

    (1,4)
    (2,3)

    seq: 100	 3
    seq: 1003	 2

     seq: 100	 4
     seq: 100	 3

    (2,1003)

    comb: 10032	 1004

    (1,100321004)

    将数据拆分成两个分区

    //分区一数据
    (1,3)
    (1,2)
    //分区二数据
    (1,4)
    (2,3)

    //分区一相同key的数据进行合并
    seq: 100     3   //(1,3)开始和中立值进行合并  合并结果为 1003
    seq: 1003     2   //(1,2)再次合并 结果为 10032

    //分区二相同key的数据进行合并
    seq: 100     4  //(1,4) 开始和中立值进行合并 1004
    seq: 100     3  //(2,3) 开始和中立值进行合并 1003

    //////////////////////////////////////////
    将两个分区的结果进行合并
    //key为2的，只在一个分区存在，不需要合并 (2,1003)
    (2,1003)
    //key为1的, 在两个分区存在，并且数据类型一致，合并
    comb: 10032     1004
    (1,100321004)
    * @param sc
    */
  def mainAggregateByKey(sc :SparkContext): Unit = {
    //-Dspark.master=local
    val data = List((1, 3), (1, 2), (1, 4), (2, 3))
    val z=sc.parallelize(data,2)

    z.foreach(println)

    //zeroValue 中立值，定义返回value的类型，并参与运算
    //seqOp 用来在一个partition中合并值的
    //comb 用来在不同partition中合并值的
    val aggregateByKeyRDD: RDD[(Int, String)] = z.aggregateByKey("100")(seqstr,combstr)

    //打印输出
    aggregateByKeyRDD.foreach(println)

    sc.stop()

  }


  def seqOp(u:Int,v:Int):Int={
    //描述如何将v合并到u，此方法依次遍历每个key，在遍历中key按照逻辑返回
    //u和v均是k-v中的v，逐一遍历RDD，当遍历的key相同时，默认为同一分组，此时下一个u,v的u为本次的返回结果
    // 分组后，不是key时，按照u为zeroValue，v为遍历的k-v中的v。
    println("Seq:"+u+ " : " +v)
    //    math.max(u,v)
    u+v
  }

  //key为3，只在一个分区存在，不需要合并
  //  (3,170) key-value仅仅在一个分区中

  //  Comb:123 : 110 key-1在1、3分区中，合并
  //  (4,188) key-4仅仅在一个分区中
  //  (1,233)

  //  Comb:123 : 129 key-2 在一、二个分区中252
  //  Comb:153 : 158 key-5在一二分区中
  //  Comb:252 : 110 key-2 在三个分区中252
  //  (53,110)  key:53在一个分区中
  //  (5,311)
  //  (2,362)//110

  //1  Seq:100 : 11  111  (1,11), 把同分区中一同处理
  //1  Seq:111 : 12  123  (1,12), 合并key-value:1,123
  //5  Seq:100 : 53  153  (5,53), 合并key-value:5,153
  //2  Seq:100 : 23  123  (2,23), 合并key-value:2,123
  //
  //2  Seq:100 : 29  129          合并key-value:2,129
  //5  Seq:100 : 58  158          合并key-value:5,158
  //3  Seq:100 : 31  131  (3,31),(3,39),把同分区中一同处理
  //3  Seq:131 : 39  170          合并key-value:3,301
  //
  //4  Seq:100 : 41  141    (4,41),(4,47),
  //4  Seq:141 : 47  188          合并key-value:4,188
  //53 Seq:100 : 10  110 (53,10), 合并key-value:5,110
  //2  Seq:100 : 10  110 (2,10),  2,110
  //1  Seq:100 : 10  110  (1,10)  合并key-value:1,110

  def combOp(a:Int,b:Int):Int={
    println("Comb:"+a+ " : " +b)
    //    Comb:1 : 9
    a+b
  }

  def mainAggregateByKey_2(sc :SparkContext): Unit = {
    //-Dspark.master=local
    //加减是对(K,V)中的V运算
    val z=sc.parallelize(List((1,11),(1,12),(5,53),(2,23),(2,29),(5,58),
      (3,31),(3,39),(4,41),(4,47),(53,10),(2,10),(1,10)),3)
    z.foreach(println)
    val agg=z.aggregateByKey(100)(seqOp, combOp)
    //打印输出
    agg.foreach(println)

    sc.stop()
  }

  def mainAggregateByKeyTuple(sc :SparkContext): Unit = {
    val z = sc.parallelize(List((1, 11.0), (1, 2.0), (1, 3.0), (2, 3.0), (2, 2.0), (3, 1.0), (3, 2.0), (4, 1.0), (4, 4.0)), 2)
    //    val z=sc.parallelize(List((1,3),(1,2),(1,4),(2,3)))
//    (seqOp: (U, V) => U,
//      combOp: (U, U) => U): RDD[(K, U)
    val agg = z.aggregateByKey((0,0.0))(
      ((u,t)=>( u )),//Key相加，对Key进行操作
      (u1,u2)=>(u1._1+u2._1,u1._2+u2._2)//Value操作
      //depth = 2
    )//math.max(_,_)
    println(agg)
    val result=agg .collect()
    //z.groupBy((x:Int,y:Int)=>x)
    result.foreach(x=>println("AggregateByKey3 Result: " + x))
  }

  def mainCombineByKey(sc :SparkContext): Unit = {
    //-Dspark.master=local
    //加减是对(K,V)中的V运算
    val z=sc.parallelize(List((1,1.0),(1,2.0),(1,3.0),(2,3.0),(2,2.0),(3,1.0),(3,2.0),(4,1.0),(4,4.0)),2)
    //    val z=sc.parallelize(List((1,3),(1,2),(1,4),(2,3)))
    val agg=z.combineByKey(
      createCombiner =(v:Double)=>(v:Double,1) ,//如果第一次遍历到(K,V)中的k则调用mergeCombiners
      mergeValue = (c:(Double,Int),v:Double)=>(c._1 + v,c._2+1),//如果不是第一次遍历到(K,V)中的k则调用mergeValue
      mergeCombiners=(c1:(Double,Int),c2:(Double,Int))=>(c1._1 + c2._1, c1._2 + c2._2),
      numPartitions=2
    )
//    CombineByKey Result: (4,(5.0,2))
//    CombineByKey Result: (2,(5.0,2))
//    CombineByKey Result: (1,(6.0,3))
//    CombineByKey Result: (3,(3.0,2))
    val result=agg .collect()
    //z.groupBy((x:Int,y:Int)=>x)
    result.foreach(x=>println("CombineByKey Result: " + x))
  }



  def mainReduceByKey(sc :SparkContext): Unit = {
    //-Dspark.master=local
    //加减是对(K,V)中的V运算
    val z=sc.parallelize(List((1,1),(1,2),(1,3),(2,1),(2,2),(3,1),(3,2),(4,1)),3)
    val result=z.reduceByKey((x,y)=>x+y).collect()
    //z.groupBy((x:Int,y:Int)=>x)
    result.foreach(x=>println("GroupByKey Result: " + x))
//    GroupByKey Result: (3,3)
//    GroupByKey Result: (4,1)
//    GroupByKey Result: (1,6)
//    GroupByKey Result: (2,3)
  }

  def mainGroupBy(sc :SparkContext): Unit = {
    //-Dspark.master=local

    val z=sc.parallelize(List((1,1),(1,2),(1,3),(2,1),(2,2),(3,1),(3,2),(4,1)),3);
    val result=z.groupByKey().collect()
    result.foreach(x=>println("GroupByKey Result: " + x))
  }

  def mainAggregate(sc :SparkContext): Unit = {
    //-Dspark.master=local

    val z=sc.parallelize(List(1,2,4,5,8,9),3)
    val result=z.aggregate(3)(seq,comb)
    println(result)
  }

}
