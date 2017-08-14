#coding=utf-8

from numpy import *

def loadDataSet(fileName):      #general function to parse tab -delimited floats
    numFeat = len(open(fileName).readline().split('\t')) - 1 #get number of fields 
    dataMat = []; labelMat = []
    fr = open(fileName)
    for line in fr.readlines():
        lineArr =[]
        curLine = line.strip().split('\t')
        for i in range(numFeat):
            lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
        labelMat.append(float(curLine[-1]))
    return dataMat,labelMat

#标准回归(X.T*X)-1*X.T
def standRegres(xArr,yArr):
    xMat = mat(xArr);
    yMat = mat(yArr).T

    xTx = xMat.T*xMat
    if linalg.det(xTx) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = xTx.I * (xMat.T*yMat)
    return ws

#岭回归(X.T+aI)-1*X.T
def ridgeRegres(xMat,yMat,lam=0.2):
    xTx = xMat.T*xMat
    denom = xTx + eye(shape(xMat)[1])*lam
    if linalg.det(denom) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = denom.I * (xMat.T*yMat)
    return ws

#局部加权线性回归行数
def lwlr(testPoint,xArr,yArr,k=1.0):
    ws=lwlrWeight(testPoint, xArr, yArr, k)
    return testPoint * ws

def lwlrWeight(testPoint, xArr, yArr, k=1.0):
    xMat = mat(xArr);
    yMat = mat(yArr).T
    m = shape(xMat)[0]
    #创建对角矩阵
    weights = mat(eye((m)))
    #next 2 lines create weights matrix
    for j in range(m):
        diffMat = testPoint - xMat[j,:] #
        #权重值大小以指数级衰竭
        weights[j,j] = exp(diffMat*diffMat.T/(-2.0*k**2))
    xTx = xMat.T * (weights * xMat)
    if linalg.det(xTx) == 0.0:
        print "This matrix is singular, cannot do inverse"
        return
    ws = xTx.I * (xMat.T * (weights * yMat))
    return  ws

#loops over all the data points and applies lwlr to each one
def lwlrTest(testArr,xArr,yArr,k=1.0):
    m = shape(testArr)[0]
    yHat = zeros(m)
    for i in range(m):
        yHat[i] = lwlr(testArr[i],xArr,yArr,k)
    return yHat

#same thing as lwlrTest except it sorts X first
def lwlrTestPlot(xArr,yArr,k=1.0):
    yHat = zeros(shape(yArr))#easier for plotting
    xCopy = mat(xArr)
    xCopy.sort(0)
    for i in range(shape(xArr)[0]):
        yHat[i] = lwlr(xCopy[i],xArr,yArr,k)
    return yHat,xCopy

#yArr and yHatArr both need to be arrays
def rssError(yArr,yHatArr):
    return ((yArr-yHatArr)**2).sum()


    
def ridge(xArr, yArr):
    xMat = mat(xArr);
    yMat=mat(yArr).T
    #数据标准化
    yMean = mean(yMat,0)
    #to eliminate X0 take mean off of Y
    yMat = yMat - yMean

    #regularize X's
    #calc mean then subtract it off
    xMeans = mean(xMat,0)
    #calc variance of Xi then divide by it
    xVar = var(xMat,0)
    xMat = (xMat - xMeans)/xVar
    numTestPts = 30
    wMat = zeros((numTestPts,shape(xMat)[1]))
    for i in range(numTestPts):
        ws = ridgeRegres(xMat,yMat,exp(i-10))
        wMat[i,:]=ws.T
    return wMat

def regularize(xMat):#regularize by columns
    inMat = xMat.copy()
    #calc mean then subtract it off
    inMeans = mean(inMat,0)
    #calc variance of Xi then divide by it
    inVar = var(inMat,0)
    inMat = (inMat - inMeans)/inVar
    return inMat

#向前逐步线性回归
def stageWise(xArr,yArr,eps=0.01,numIt=100):
    xMat = mat(xArr);
    yMat=mat(yArr).T
    yMean = mean(yMat,0)
    #can also regularize ys but will get smaller coef
    yMat = yMat - yMean
    xMat = regularize(xMat)
    m,n=shape(xMat)
    returnMat = zeros((numIt,n)) #testing code remove
    ws = zeros((n,1));
    wsTest = ws.copy();
    wsMax = ws.copy()

    for i in range(numIt):
        #print ws.T
        lowestError = inf; 
        for j in range(n):
            for sign in [-1,1]:
                wsTest = ws.copy()
                wsTest[j] += eps*sign
                yTest = xMat*wsTest
                rssE = rssError(yMat.A,yTest.A)
                if rssE < lowestError:
                    lowestError = rssE
                    wsMax = wsTest
        ws = wsMax.copy()
        returnMat[i,:]=ws.T
    return returnMat



#交叉验证岭回归
def crossValidation(xArr,yArr,numVal=10):
    m = len(yArr)                           
    indexList = range(m)
    #create error mat 30columns numVal rows
    errorMat = zeros((numVal,30))
    for i in range(numVal):
        #训练集和测试集
        trainX=[]; trainY=[]
        testX = []; testY = []
        random.shuffle(indexList)
        #create training set based on first 90% of values in indexList
        for j in range(m):
            if j < m*0.9: 
                trainX.append(xArr[indexList[j]])
                trainY.append(yArr[indexList[j]])
            else:
                testX.append(xArr[indexList[j]])
                testY.append(yArr[indexList[j]])
        #get 30 weight vectors from ridge
        #从岭回归中获取
        wMat = ridge(trainX, trainY)
        #loop over all of the ridge estimates
        for k in range(30):
            #用训练时的参数将测试数据格式化
            matTestX = mat(testX); matTrainX=mat(trainX)
            meanTrain = mean(matTrainX,0)
            varTrain = var(matTrainX,0)
            #regularize test with training params
            matTestX = (matTestX-meanTrain)/varTrain
            #test ridge results and store
            #测试集测试结果
            yEst = matTestX * mat(wMat[k,:]).T + mean(trainY)
            #测试样本的偏差
            errorMat[i,k]=rssError(yEst.T.A,array(testY))
            #print errorMat[i,k]

    #calc avg performance of the different ridge weight vectors
    meanErrors = mean(errorMat,0)
    minMean = float(min(meanErrors))
    bestWeights = wMat[nonzero(meanErrors==minMean)]
    #can unregularize to get model
    #when we regularized we wrote Xreg = (x-meanX)/var(x)
    #we can now write in terms of x not Xreg:  x*w/var(x) - meanX/var(x) +meanY
    #数据还原
    xMat = mat(xArr);
    yMat=mat(yArr).T
    meanX = mean(xMat,0);
    varX = var(xMat,0)
    unReg = bestWeights/varX
    print "the best model from Ridge Regression is:\n",unReg
    print "with constant term: ",-1*sum(multiply(meanX,unReg)) + mean(yMat)