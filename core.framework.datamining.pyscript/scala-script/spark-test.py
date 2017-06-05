#coding=utf-8
from __future__ import print_function
from pyspark import SparkContext, SparkConf

import sys
from operator import add

from pyspark.sql import SparkSession


if __name__ == "__main__":
    # spark = SparkSession \
    #     .builder \
    #     .appName("PythonWordCount") \
    #     .getOrCreate()

    conf = SparkConf().setAppName("PythonWordCount")
    sc = SparkContext(conf=conf)

    # lines = spark.read.text('D:\DevN\sample-data\spark-data\wordcount.txt').rdd.map(lambda r: r[0])
    lines=sc.textFile('D:\DevN\sample-data\spark-data\wordcount.txt')
    counts = lines.flatMap(lambda x: x.split(' ')) \
        .map(lambda x: (x, 1)) \
        .reduceByKey(add)
    output = counts.collect()
    for (word, count) in output:
        print("%s: %i" % (word, count))

    # spark.stop()
    # sc.
