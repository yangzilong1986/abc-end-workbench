#coding=utf-8
from numpy import *

def loadSimpData():
    datMat = matrix([[ 1. ,  2.1],
        [ 2. ,  1.1],
        [ 1.3,  1. ],
        [ 1. ,  1. ],
        [ 2. ,  1. ]])
    classLabels = [1.0, 1.0, -1.0, -1.0, 1.0]
    return datMat,classLabels

#自适应数据加载函数
def loadDataSet(fileName):      #general function to parse tab -delimited floats
    numFeat = len(open(fileName).readline().split('\t')) #get number of fields 
    dataMat = []; labelMat = []
    fr = open(fileName)
    for line in fr.readlines():
        lineArr =[]
        curLine = line.strip().split('\t')
        for i in range(numFeat-1):
            lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
        labelMat.append(float(curLine[-1]))
    return dataMat,labelMat

#just classify the data,dimen为列
def stumpClassify(dataMatrix,dimen,threshVal,threshIneq):
    # retArray = ones((shape(dataMatrix)[0],1))#[5*1]
    dataShape=shape(dataMatrix)[0]
    retArray = ones((dataShape,1))#
    if threshIneq == 'lt':
        retArray[dataMatrix[:,dimen] <= threshVal] = -1.0#列小于阀值
    else:
        retArray[dataMatrix[:,dimen] > threshVal] = -1.0
    return retArray
    
#单层决策树
def buildStump(dataArr,classLabels,D):
    dataMatrix = mat(dataArr);#[5*2]
    labelMat = mat(classLabels).T#[5,1]
    m,n = shape(dataMatrix)#5,2，即5行2列
    numSteps = 10.0;
    bestStump = {};
    bestClasEst = mat(zeros((m,1)))
    minError = inf #init error sum, to +infinity
    #数据集中的每一个特征匀速
    for i in range(n):#loop over all dimensions
        #[:,i]取i列
        rangeMin = dataMatrix[:,i].min();
        rangeMax = dataMatrix[:,i].max();
        stepSize = (rangeMax-rangeMin)/numSteps
        #loop over all range in current dimension
        #对每个步长运算
        for j in range(-1,int(numSteps)+1):
            #go over less than and greater than
            #对每个不等号运算
            for inequal in ['lt', 'gt']:
                #建立一颗单层决策树并利用加权数据集对它进行测试
                # labelMat
                # [[ 1.]
                #  [ 1.]
                #  [-1.]
                #  [-1.]
                #  [ 1.]]
                threshVal = (rangeMin + float(j) * stepSize)
                #call stump classify with i, j, lessThan，分类
                # predictedVals
                # [[ 1.]
                #  [ 1.]
                #  [ 1.]
                #  [ 1.]
                #  [ 1.]]
                predictedVals = stumpClassify(dataMatrix,i,threshVal,inequal)
                errArr = mat(ones((m,1)))#

                errArr[predictedVals == labelMat] = 0
                # errArr
                # [[ 0.]
                #  [ 0.]
                #  [ 1.]
                #  [ 1.]
                #  [ 0.]]
                #calc total error multiplied by D
                #计算错误率，D[5*1] errArr[5*1]
                weightedError = D.T*errArr
                #print "split: dim %d, thresh %.2f, thresh ineqal:
                # %s, the weighted error is %.3f"
                # % (i, threshVal, inequal, weightedError)
                #如果错误率低于minError，则将当前单层决策树设为最佳单层决策树
                if weightedError < minError:
                    minError = weightedError
                    bestClasEst = predictedVals.copy()
                    bestStump['dim'] = i
                    bestStump['thresh'] = threshVal
                    bestStump['ineq'] = inequal
    #最佳单层决策树
    return bestStump,minError,bestClasEst

#单层决策树的AdaBoost训练过程
def adaBoostTrainDS(dataArr,classLabels,numIt=40):
    weakClassArr = []
    m = shape(dataArr)[0]
    D = mat(ones((m,1))/m)   #init D to all equal
    aggClassEst = mat(zeros((m,1)))
    for i in range(numIt):
        bestStump,error,classEst = buildStump(dataArr,classLabels,D)#build Stump
        #print "D:",D.T
        #calc alpha, throw in max(error,eps) to account for error=0
        #错误率计算公式
        alpha = float(0.5*log((1.0-error)/max(error,1e-16)))
        bestStump['alpha'] = alpha
        #store Stump Params in Array
        weakClassArr.append(bestStump)
        #print "classEst: ",classEst.T
        #exponent for D calc, getting messy
        classLabelsT=mat(classLabels).T
        # expon = multiply(-1*alpha*mat(classLabels).T,classEst)
        expon = multiply(-1*alpha*classLabelsT,classEst)
        #Calc New D for next iteration
        #为下一次迭代计算D
        D = multiply(D,exp(expon))
        D = D/D.sum()
        #calc training error of all classifiers,
        # if this is 0 quit for loop early (use break)
        aggClassEst += alpha*classEst
        #print "aggClassEst: ",aggClassEst.T
        aggErrors = multiply(sign(aggClassEst) != mat(classLabels).T,ones((m,1)))
        errorRate = aggErrors.sum()/m
        print "total error: ",errorRate
        if errorRate == 0.0: break
    return weakClassArr,aggClassEst

#分类函数
def adaClassify(datToClass,classifierArr):
    #do stuff similar to last aggClassEst in adaBoostTrainDS
    dataMatrix = mat(datToClass)
    m = shape(dataMatrix)[0]
    aggClassEst = mat(zeros((m,1)))
    for i in range(len(classifierArr)):
        classEst = stumpClassify(dataMatrix,classifierArr[i]['dim'],\
                                 classifierArr[i]['thresh'],\
                                 classifierArr[i]['ineq'])#call stump classify
        aggClassEst += classifierArr[i]['alpha']*classEst
        print aggClassEst
    return sign(aggClassEst)

#ROC曲线绘制
def plotROC(predStrengths, classLabels):
    import matplotlib.pyplot as plt
    cur = (1.0,1.0) #cursor
    ySum = 0.0 #variable to calculate AUC
    numPosClas = sum(array(classLabels)==1.0)
    yStep = 1/float(numPosClas);
    xStep = 1/float(len(classLabels)-numPosClas)
    sortedIndicies = predStrengths.argsort()#get sorted index, it's reverse
    fig = plt.figure()
    fig.clf()
    ax = plt.subplot(111)
    #loop through all the values, drawing a line segment at each point
    for index in sortedIndicies.tolist()[0]:
        if classLabels[index] == 1.0:
            delX = 0;
            delY = yStep;
        else:
            delX = xStep;
            delY = 0;
            ySum += cur[1]
        #draw line from cur to (cur[0]-delX,cur[1]-delY)
        ax.plot([cur[0],cur[0]-delX],[cur[1],cur[1]-delY], c='b')
        cur = (cur[0]-delX,cur[1]-delY)
    ax.plot([0,1],[0,1],'b--')
    plt.xlabel('False positive rate'); plt.ylabel('True positive rate')
    plt.title('ROC curve for AdaBoost horse colic detection system')
    ax.axis([0,1,0,1])
    plt.show()
    print "the Area Under the Curve is: ",ySum*xStep
