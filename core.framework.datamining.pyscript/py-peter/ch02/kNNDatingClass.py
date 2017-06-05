#coding=utf-8

from numpy import *
import operator
from os import listdir
from kNN import *

def file2matrix(filename):
    fr = open(filename)
    numberOfLines = len(fr.readlines())         #get the number of lines in the file
    returnMat = zeros((numberOfLines,3))        #prepare matrix to return
    classLabelVector = []                       #prepare labels return
    fr = open(filename)
    index = 0
    for line in fr.readlines():
        line = line.strip()
        listFromLine = line.split('\t')
        returnMat[index,:] = listFromLine[0:3]
        classLabelVector.append(int(listFromLine[-1]))
        index += 1
    return returnMat,classLabelVector

#归一化特征值
def autoNorm(dataSet):
    minVals = dataSet.min(0)#每列数据的最小值
    maxVals = dataSet.max(0)#每列数据中的最大值
    ranges = maxVals - minVals
    #复制出和样本测试样本相同的矩阵1000*3
    normDataSet = zeros(shape(dataSet))
    m = dataSet.shape[0]#行数
    normDataSet = dataSet - tile(minVals, (m,1))
    normDataSet = normDataSet/tile(ranges, (m,1)) #特征值相除
    return normDataSet, ranges, minVals

def datingClassTest():
    hoRatio = 0.10  #hold out 10%
    #读取数据
    datingDataMat,datingLabels = file2matrix('datingTestSet2.txt')
    #归一化数据
    normMat, ranges, minVals = autoNorm(datingDataMat)

    #行数
    m = normMat.shape[0]

    #测试样本集
    numTestVecs = int(m*hoRatio)

    errorCount = 0.0
    for i in range(numTestVecs):
        #cf=kNN.classify0([0,0],group,labels,1)
        classifierResult = classify0(normMat[i,:],#测试样本，取第几行
                                     normMat[numTestVecs:m,:],#样本数据 从第行取100，900 100
                                     datingLabels[numTestVecs:m],#测试样本标签，从900开始取到1000
                                     3#选取与当前距离最小的k个点
                                     )
        print "the classifier came back with: %d, " \
              "the real answer is: %d" \
              % (classifierResult, datingLabels[i])
        #评估算法的错误率
        if (classifierResult != datingLabels[i]):
            errorCount += 1.0
    print "the total error rate is: %f" % (errorCount/float(numTestVecs))
    print errorCount

