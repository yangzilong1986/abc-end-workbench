#coding=utf-8

from numpy import *

'''
Logistic回归的计算
'''
def sigmoid(inX):
    return 1.0/(1+exp(-inX))

'''
梯度上升计算
'''
def gradAscent(dataMatIn, classLabels):
    #转换为矩阵
    #dataMatrix[100*3]
    dataMatrix = mat(dataMatIn) #convert to NumPy matrix
    #dataMatrix 100 3矩阵
    # [[  1.00000000e+00  -1.76120000e-02   1.40530640e+01]
    #  [  1.00000000e+00  -1.39563400e+00   4.66254100e+00]
    # [  1.00000000e+00  -7.52157000e-01   6.53862000e+00]
    # [  1.00000000e+00  -1.32237100e+00   7.15285300e+00]
    # [  1.00000000e+00   4.23363000e-01   1.10546770e+01]
    # [  1.00000000e+00   4.06704000e-01   7.06733500e+00]
    # [  1.00000000e+00   6.67394000e-01   1.27414520e+01]
    # [  1.00000000e+00  -2.46015000e+00   6.86680500e+00]

    #transpose转置之后，100行1列
    #转换为矩阵之后转置
    #labelMat[100*1]
    #classLabels 100
    labelMat = mat(classLabels).transpose() #convert to NumPy matrix

    m,n = shape(dataMatrix)#矩阵的行、列
    alpha = 0.001#可以设置
    maxCycles = 500
    #weights[3*1]，表达式中θ
    weights = ones((n,1))
    # [[ 1.]
    #  [ 1.]
    #  [ 1.]]
    #dataMatrix 100 3矩阵
    #dataMatrix.transpose()为3 100
    #dataTranpose[3*100]
    dataTranpose=dataMatrix.transpose()
    print('dataMatrix dataTranpose is\t',dataTranpose)
    #[1.00000000e+00,   1.00000000e+00,   1.00000000e+00, 1.00000000e+00,     1.00000000e+00,...]
    #[-1.76120000e-02,  -1.39563400e+00,  -7.52157000e-01,-1.32237100e+00,    4.23363000e-01,...]
    #[1.40530640e+01,   4.66254100e+00,   6.53862000e+00, 7.15285300e+00,     1.10546770e+01,...]

    #计算次数
    for k in range(maxCycles):  #heavy on matrix operations
        #dataMatrix为100 3的矩阵
        #dataMatrix[100*3] weights[3*1]
        #sigmoidParam[100*1]
        #分量求和
        # weights值
        # [[ 1.]
        #  [ 1.]
        #  [ 1.]]
        sigmoidParam=dataMatrix*weights#矩阵积
        #h为100 1的矩阵 h[100*1]
        h = sigmoid(sigmoidParam)     #matrix mult，矩阵乘法

        #error[100*1]。计算结果
        error = (labelMat - h) #vector subtraction

        #weights[3*1] dataTranpose[3*100]*error[100*1]
        #每个分量X0,X1,X2之和
        weights = weights + alpha *dataTranpose * error #matrix mult
        print('weights is\t',weights)
    return weights

'''
改进的随机梯度算法
dataMatrix为数组
'''
def stocGradAscent(dataMatrix, classLabels, numIter=150):
    m,n = shape(dataMatrix)
    weights = ones(n)   #initialize to all ones
    for j in range(numIter):
        dataIndex = range(m)
        for i in range(m):
            alpha = 4/(1.0+j+i)+0.0001    #apha decreases with iteration, does not
            #random.uniform生成随机数
            randIndex = int(random.uniform(0,len(dataIndex)))#go to 0 because of the constant
            h = sigmoid(sum(dataMatrix[randIndex]*weights))
            error = classLabels[randIndex] - h
            weights = weights + alpha * error * dataMatrix[randIndex]
            del(dataIndex[randIndex])
    return weights

def classifyVector(inX, weights):
    prob = sigmoid(sum(inX*weights))
    if prob > 0.5: return 1.0
    else: return 0.0

'''
    X1        X2      分类
-0.017612	14.053064	  0
-1.395634	4.662541	  1

loadDataSet读取信息
'''
def loadDataSet():
    dataMat = []; labelMat = []
    fr = open('testSet.txt')
    for line in fr.readlines():
        lineArr = line.strip().split()
        dataMat.append([1.0, float(lineArr[0]), float(lineArr[1])])
        labelMat.append(int(lineArr[2]))
    return dataMat,labelMat

def plotBestFit(weights):
    import matplotlib.pyplot as plt
    dataMat,labelMat=loadDataSet()
    dataArr = array(dataMat)
    n = shape(dataArr)[0]
    xcord1 = []; ycord1 = []
    xcord2 = []; ycord2 = []
    for i in range(n):
        if int(labelMat[i])== 1:
            xcord1.append(dataArr[i,1]); ycord1.append(dataArr[i,2])
        else:
            xcord2.append(dataArr[i,1]); ycord2.append(dataArr[i,2])
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(xcord1, ycord1, s=30, c='red', marker='s')
    ax.scatter(xcord2, ycord2, s=30, c='green')
    x = arange(-3.0, 3.0, 0.1)
    y = (-weights[0]-weights[1]*x)/weights[2]
    ax.plot(x, y)
    plt.xlabel('X1'); plt.ylabel('X2');
    plt.show()

def colicTest():
    frTrain = open('horseColicTraining.txt');
    frTest = open('horseColicTest.txt')
    trainingSet = []; trainingLabels = []
    for line in frTrain.readlines():
        currLine = line.strip().split('\t')
        lineArr =[]
        for i in range(21):
            lineArr.append(float(currLine[i]))
        trainingSet.append(lineArr)
        trainingLabels.append(float(currLine[21]))

    trainWeights = stocGradAscent(array(trainingSet), trainingLabels, 1000)

    errorCount = 0; numTestVec = 0.0
    for line in frTest.readlines():
        numTestVec += 1.0
        currLine = line.strip().split('\t')
        lineArr =[]
        for i in range(21):
            lineArr.append(float(currLine[i]))
        if int(classifyVector(array(lineArr), trainWeights))!= int(currLine[21]):
            errorCount += 1

    errorRate = (float(errorCount)/numTestVec)
    print "the error rate of this test is: %f" % errorRate
    return errorRate

def multiTest():
    numTests = 10; errorSum=0.0
    for k in range(numTests):
        errorSum += colicTest()
    print "after %d iterations the average error rate is: %f" % (numTests, errorSum/float(numTests))
        