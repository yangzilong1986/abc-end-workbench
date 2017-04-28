#coding=utf-8

from numpy import *
import operator
from os import listdir

'''
intX:分类的输入向量
dataSet:样本数据
标签
k:最近邻居的数目
'''
def classify0(inX, dataSet, labels, k):
    #数据集行数即数据集记录数
    dataSetSize = dataSet.shape[0]#数组的维度
    #在列方向上重复inX[0,0]1次，行dataSetSize次
    #获取矩阵的差值
    '''距离计算'''
    # dataSet结构
    # [[ 1.   1.1]
    #  [ 1.   1. ]
    # [ 0.   0. ]
    # [ 0.   0.1]]
    #样本与原先所有样本的差值矩阵
    #初始化List[]
    datTile=tile(inX, (dataSetSize,1))
    # datTile结构
    # [[0 0]
    #  [0 0]
    # [0 0]
    # [0 0]]
    diffMat = datTile - dataSet
    # diffMat结果
    # [[-1.  -1.1]
    #  [-1.  -1. ]
    #  [ 0.   0. ]
    # [ 0.  -0.1]]
    # diffMat = tile(inX, (dataSetSize,1)) - dataSet
    #差值取平方

    sqDiffMat = diffMat**2
    #sqDiffMat结果
    # [[ 1.    1.21]
    #  [ 1.    1.  ]
    # [ 0.    0.  ]
    # [ 0.    0.01]]
    #行加到一起，
    #计算每一行上元素的和， 1.  +  1.21，1.  +  1.
    sqDistances = sqDiffMat.sum(axis=1)
    distances = sqDistances**0.5
    #按照值，返回排序索引,即 对距离大小进行排序
    sortedDistIndicies = distances.argsort()     
    classCount={}#{'A':1,'B':1 },
    # 注：tuple（元祖） 用小括号，Dictionary (字典) : 用{}来定义，list（列表） 用方括号
    #投票表决
    #即选择距离最小的 K 个点
    for i in range(k):
        voteIlabel = labels[sortedDistIndicies[i]]#B
        classCount[voteIlabel] = classCount.get(voteIlabel,0) + 1

    # 按照类别的数量多少进行排序
    sortedClassCount = sorted(classCount.iteritems(),
                              key=operator.itemgetter(1),
                              reverse=True)
    sortedRow=sortedClassCount[0]
    # 返回类别数最多的类别名称
    return sortedClassCount[0][0]

def createDataSet():
    group = array([[1.0,1.1],[1.0,1.0],[0,0],[0,0.1]])
    labels = ['A','A','B','B']
    return group, labels

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
    
def autoNorm(dataSet):
    minVals = dataSet.min(0)
    maxVals = dataSet.max(0)
    ranges = maxVals - minVals
    normDataSet = zeros(shape(dataSet))
    m = dataSet.shape[0]
    normDataSet = dataSet - tile(minVals, (m,1))
    normDataSet = normDataSet/tile(ranges, (m,1))   #element wise divide
    return normDataSet, ranges, minVals
   
def datingClassTest():
    hoRatio = 0.50  #hold out 10%
    #load data setfrom file
    datingDataMat,datingLabels = file2matrix('datingTestSet2.txt')
    normMat, ranges, minVals = autoNorm(datingDataMat)
    m = normMat.shape[0]
    numTestVecs = int(m*hoRatio)
    errorCount = 0.0
    for i in range(numTestVecs):
        classifierResult = classify0(normMat[i,:],normMat[numTestVecs:m,:],datingLabels[numTestVecs:m],3)
        print "the classifier came back with: %d, the real answer is: %d" \
              % (classifierResult, datingLabels[i])
        if (classifierResult != datingLabels[i]): errorCount += 1.0
    print "the total error rate is: %f" % (errorCount/float(numTestVecs))
    print errorCount
    
def img2vector(filename):
    returnVect = zeros((1,1024))
    fr = open(filename)
    for i in range(32):
        lineStr = fr.readline()
        for j in range(32):
            returnVect[0,32*i+j] = int(lineStr[j])
    return returnVect

def handwritingClassTest():
    # 导入数据
    hwLabels = []
    #load the training set
    trainingFileList = listdir('trainingDigits')
    m = len(trainingFileList)
    trainingMat = zeros((m,1024))
    for i in range(m):
        fileNameStr = trainingFileList[i]
        #take off .txt
        fileStr = fileNameStr.split('.')[0]
        classNumStr = int(fileStr.split('_')[0])
        hwLabels.append(classNumStr)
        trainingMat[i,:] = img2vector('trainingDigits/%s' % fileNameStr)

    #iterate through the test set
    testFileList = listdir('testDigits')
    errorCount = 0.0
    mTest = len(testFileList)
    for i in range(mTest):
        fileNameStr = testFileList[i]
        #take off .txt
        fileStr = fileNameStr.split('.')[0]
        classNumStr = int(fileStr.split('_')[0])
        vectorUnderTest = img2vector('testDigits/%s' % fileNameStr)
        classifierResult = classify0(vectorUnderTest, trainingMat, hwLabels, 3)
        print "the classifier came back with: %d, the real answer is: %d"\
              % (classifierResult, classNumStr)
        if (classifierResult != classNumStr): errorCount += 1.0
    print "\nthe total number of errors is: %d" % errorCount
    print "\nthe total error rate is: %f" % (errorCount/float(mTest))