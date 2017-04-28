#coding=utf-8

from numpy import *
from time import sleep

def loadDataSet(fileName):
    dataMat = []; labelMat = []
    fr = open(fileName)
    for line in fr.readlines():
        lineArr = line.strip().split('\t')
        dataMat.append([float(lineArr[0]), float(lineArr[1])])
        labelMat.append(float(lineArr[2]))
    return dataMat,labelMat

def selectJrand(i,m):
    j=i #we want to select any J not equal to i
    while (j==i):
        j = int(random.uniform(0,m))
    return j

'''
调整大于H或者小于L的alphas
'''
def clipAlpha(aj,H,L):
    if aj > H: 
        aj = H
    if L > aj:
        aj = L
    return aj

def smoSimple(dataMatIn, classLabels, C, toler, maxIter):
    dataMatrix = mat(dataMatIn);
    labelMat = mat(classLabels).transpose()
    b = 0;
    #tuple,100,2
    m,n = shape(dataMatrix)
    #alphas估值矩阵[100,1]
    alphas = mat(zeros((m,1)))
    iter = 0
    while (iter < maxIter):
        alphaPairsChanged = 0
        for i in range(m):
            #alphas[100,1]  labelMat[100,1]
            alphasLableMat=multiply(alphas,labelMat)#100,1,矩阵元素乘
            alphasLableMat_T=alphasLableMat.T
            #dataMatrix[i,:]取行，dataMatrix[:,i]取列
            dataMartix_Col=dataMatrix[i,:]
            # dataMartix_Col值[1,2]
            # <type 'list'>: [matrix([[ 3.542485,  1.977398]])]
            dataMartix_Col_T=dataMartix_Col.T
            # dataMartix_Col_T值[2,1]
            # <type 'list'>: [matrix([[ 3.542485]]), matrix([[ 1.977398]])]

            #dataMult[100,1]
            dataMult=dataMatrix*dataMartix_Col_T#dataMatrix[100,2]
            fXi = float(alphasLableMat_T*dataMult) + b
            # fXi = float(multiply(alphas,labelMat).T*(dataMatrix*dataMatrix[i,:].T)) + b
            #if checks if an example violates KKT conditions
            Ei = fXi - float(labelMat[i])
            if ((labelMat[i]*Ei < -toler) and (alphas[i] < C)) \
                    or ((labelMat[i]*Ei > toler) and (alphas[i] > 0)):
                j = selectJrand(i,m)
                #另一个
                fXj = float(multiply(alphas,labelMat).T*(dataMatrix*dataMatrix[j,:].T)) + b
                Ej = fXj - float(labelMat[j])
                alphaIold = alphas[i].copy();
                alphaJold = alphas[j].copy();
                if (labelMat[i] != labelMat[j]):
                    L = max(0, alphas[j] - alphas[i])
                    H = min(C, C + alphas[j] - alphas[i])
                else:
                    L = max(0, alphas[j] + alphas[i] - C)
                    H = min(C, alphas[j] + alphas[i])
                if L==H: print "L==H"; continue
                eta = 2.0 * dataMatrix[i,:]*dataMatrix[j,:].T \
                      - dataMatrix[i,:]*dataMatrix[i,:].T \
                      - dataMatrix[j,:]*dataMatrix[j,:].T
                if eta >= 0: print "eta>=0"; continue
                alphas[j] -= labelMat[j]*(Ei - Ej)/eta
                alphas[j] = clipAlpha(alphas[j],H,L)
                if (abs(alphas[j] - alphaJold) < 0.00001):
                    print "j not moving enough";
                    continue
                #update i by the same amount as j
                #the update is in the oppostie direction
                alphas[i] += labelMat[j]*labelMat[i]*(alphaJold - alphas[j])

                b1 = b - Ei\
                     - labelMat[i]*(alphas[i]-alphaIold)*dataMatrix[i,:]*dataMatrix[i,:].T \
                     - labelMat[j]*(alphas[j]-alphaJold)*dataMatrix[i,:]*dataMatrix[j,:].T
                b2 = b - Ej\
                     - labelMat[i]*(alphas[i]-alphaIold)*dataMatrix[i,:]*dataMatrix[j,:].T \
                     - labelMat[j]*(alphas[j]-alphaJold)*dataMatrix[j,:]*dataMatrix[j,:].T

                if (0 < alphas[i]) and (C > alphas[i]):
                    b = b1
                elif (0 < alphas[j]) and (C > alphas[j]):
                    b = b2
                else: b = (b1 + b2)/2.0
                alphaPairsChanged += 1
                print "iter: %d i:%d, pairs changed %d" % (iter,i,alphaPairsChanged)
        if (alphaPairsChanged == 0): iter += 1
        else: iter = 0
        print "iteration number: %d" % iter
    return b,alphas

#calc the kernel or transform data to a higher dimensional space
def kernelTrans(X, A, kTup):
    m,n = shape(X)
    K = mat(zeros((m,1)))
    #linear kernel
    if kTup[0]=='lin':
        #X矩阵数据[100*2]
        # 000 = {matrix} [[ 3.542485  1.977398]]
        # 001 = {matrix} [[ 3.018896  2.556416]]
        # 002 = {matrix} [[ 7.55151 -1.58003]]
        # 003 = {matrix} [[ 2.114999 -0.004466]]
        # 004 = {matrix} [[ 8.127113  1.274372]]
        #A矩阵[2*1]
        # <type 'list'>: [matrix([[ 3.542485,  1.977398]])]
        at=A.T
        # 0 = {matrix} [[ 3.542485]]
        # 1 = {matrix} [[ 1.977398]]
        K = X * at  #K[100*1]
        #K为[100,1]，它是训练数据集本身和每行的乘积
    elif kTup[0]=='rbf':
        for j in range(m):
            deltaRow = X[j,:] - A
            K[j] = deltaRow*deltaRow.T
        #divide in NumPy is element-wise not matrix like Matlab
        K = exp(K/(-1*kTup[1]**2))
    else: raise NameError('Houston We Have a Problem -- \
    That Kernel is not recognized')
    return K

class optStruct:
    # Initialize the structure with the parameters
    def __init__(self,dataMatIn, classLabels, C, toler, kTup):
        self.X = dataMatIn  #数据矩阵
        self.labelMat = classLabels #分类矩阵
        self.C = C  #常数C
        self.tol = toler    #容错率
        #shape返回为元组
        #结果返回一个tuple元组 (100L, 2L)
        self.m = shape(dataMatIn)[0]    #m:100 第一列
        #alphas为[100*1]矩阵
        self.alphas = mat(zeros((self.m,1)))
        self.b = 0
        self.eCache = mat(zeros((self.m,2))) #first column is valid flag
        #K[100*100]
        self.K = mat(zeros((self.m,self.m)))
        for i in range(self.m):#m为100，即行数
            #s[i : j : k]代表的意思是列表 s 中的第 i 个元素(包含), 到第 j 个元素(不包含)，
            #每隔 k 个数取一个 形成的列表，即取[j , k)，步长为 k取出的列表子集。
            #[:,i]取i列

            #X[i,:]取第i行
            #3.542485	1.977398
            #3.018896	2.556416
            #每列赋值，K为训练数据的数据和每行数据乘积
            self.K[:,i] = kernelTrans(self.X, self.X[i,:], kTup)

#分类与alphas算法
def calcEk(oS, k):
    #oS.alphas[100,1]与分类矩阵oS.labelMat[100,l],矩阵中对应元素相乘
    #ufunc通用函数是对数组中的数据执行元素级运算的函数。
    #abs,fabs,sqrt,square,exp,log,sign,ceil,floor,rint,modf,isnan,isfinite,isinf,cos,cosh,sin,sinh,tan,tanh，
    #add,subtract,multiply,power,mod,equal,等等
    #alphas与分类的元素乘积
    alphasLabelMult=multiply(oS.alphas,oS.labelMat)
    alphasLabelMultT=alphasLabelMult.T
    #获取第k列的数据，即osK[100,1]
    osK=oS.K[:,k]#osK[100*1]
    #alphasLabelMultT[1,100]和训练数据第k列的数据
    aK=alphasLabelMultT*osK
    # fXk = float(multiply(oS.alphas,oS.labelMat).T*oS.K[:,k] + oS.b)
    fXk = float(aK + oS.b)
    labelK=oS.labelMat[k]
    Ek = fXk - float(labelK)
    return Ek

#this is the second choice -heurstic, and calcs Ej
#内循环中的启发式方法，训练数据的第i行
def selectJ(i, oS, Ei):
    maxK = -1; maxDeltaE = 0; Ej = 0
    #set valid #choose the alpha that gives the maximum delta E
    oS.eCache[i] = [1,Ei]
    validEcacheList = nonzero(oS.eCache[:,0].A)[0]
    if (len(validEcacheList)) > 1:
        #loop through valid Ecache values and find the one that maximizes delta E
        for k in validEcacheList:
            if k == i:
                continue #don't calc for i, waste of time
            Ek = calcEk(oS, k)
            deltaE = abs(Ei - Ek)
            #选择具有最大步长的j
            if (deltaE > maxDeltaE):
                maxK = k;
                maxDeltaE = deltaE;
                Ej = Ek
        return maxK, Ej
    else:   #in this case (first time around) we don't have any valid eCache values
        j = selectJrand(i, oS.m)    #当在缓存中没有时，则再随机选出一行，此行不是i，进而继续往下计算
        Ej = calcEk(oS, j)#计算
    return j, Ej

#after any alpha has changed update the new value in the cache
def updateEk(oS, k):
    Ek = calcEk(oS, k)
    oS.eCache[k] = [1,Ek]

#SMO序列最小优化,i第几行
def innerL(i, oS):
    Ei = calcEk(oS, i)#Ei为一个数值
    labelEi=oS.labelMat[i]*Ei
    if ((labelEi < -oS.tol) and (oS.alphas[i] < oS.C)) \
            or ((labelEi > oS.tol) and (oS.alphas[i] > 0)):
        #this has been changed from selectJrand
        #第二个alphas选择中的启发式方法，计算误差
        j,Ej = selectJ(i, oS, Ei)
        alphaIold = oS.alphas[i].copy();
        alphaJold = oS.alphas[j].copy();

        #保证alpha在0与C之间
        if (oS.labelMat[i] != oS.labelMat[j]):
            L = max(0, oS.alphas[j] - oS.alphas[i])
            H = min(oS.C, oS.C + oS.alphas[j] - oS.alphas[i])
        else:
            L = max(0, oS.alphas[j] + oS.alphas[i] - oS.C)
            H = min(oS.C, oS.alphas[j] + oS.alphas[i])
        #
        if L==H:
            print "L==H";
            return 0

        eta = 2.0 * oS.K[i,j] - oS.K[i,i] - oS.K[j,j] #changed for kernel
        if eta >= 0:
            print "eta>=0";
            return 0

        oS.alphas[j] -= oS.labelMat[j]*(Ei - Ej)/eta
        oS.alphas[j] = clipAlpha(oS.alphas[j],H,L)
        #更新误差缓存
        updateEk(oS, j) #added this for the Ecache

        if (abs(oS.alphas[j] - alphaJold) < 0.00001):
            print "j not moving enough";
            return 0

        #update i by the same amount as j
        oS.alphas[i] += oS.labelMat[j]*oS.labelMat[i]*(alphaJold - oS.alphas[j])

        #added this for the Ecache
        #the update is in the oppostie direction
        #更新误差缓存
        updateEk(oS, i)
        alpIsub=oS.alphas[i]-alphaIold
        alpJsub=oS.alphas[j]-alphaJold

        #i和j为不同的行，并且i是第一次遍历行，j是后一次遍历值
        b1 = oS.b - Ei- \
             oS.labelMat[i]*(alpIsub)*oS.K[i,i] - \
             oS.labelMat[j]*(alpJsub)*oS.K[i,j]

        b2 = oS.b - Ej- \
             oS.labelMat[j]*(alpJsub)*oS.K[j,j]- \
             oS.labelMat[i]*(alpIsub)*oS.K[i,j]

        if (0 < oS.alphas[i]) and (oS.C > oS.alphas[i]):
            oS.b = b1
        elif (0 < oS.alphas[j]) and (oS.C > oS.alphas[j]):
            oS.b = b2
        else:
            oS.b = (b1 + b2)/2.0

        return 1
    else: return 0

#full Platt SMO
'''
dataMatIn:训练数据矩阵
classLabels:分类矩阵
C:常数
toler:容错率
maxIter:退出最大循环次数
'''
def smoP(dataMatIn, classLabels, C, toler, maxIter,kTup=('lin', 0)):
    #实例化结构,测试中kTup取默认值
    oS = optStruct(mat(dataMatIn),mat(classLabels).transpose(),C,toler, kTup)
    iter = 0
    entireSet = True;
    alphaPairsChanged = 0
    while (iter < maxIter) and ((alphaPairsChanged > 0) or (entireSet)):
        alphaPairsChanged = 0
        #遍历所有值
        if entireSet:   #go over all
            for i in range(oS.m):
                #训练数据集的每行
                alphaPairsChanged += innerL(i,oS)
                print "fullSet, iter: %d i:%d, pairs changed %d" % (iter,i,alphaPairsChanged)
            iter += 1
        else:#go over non-bound (railed) alphas遍历非边界值
            alNoEdge=(oS.alphas.A > 0) * (oS.alphas.A < C)
            nonBoundIs = nonzero(alNoEdge)[0]
            # nonBoundIs = nonzero((oS.alphas.A > 0) * (oS.alphas.A < C))[0]
            for i in nonBoundIs:
                alphaPairsChanged += innerL(i,oS)
                print "non-bound, iter: %d i:%d, pairs changed %d" % (iter,i,alphaPairsChanged)
            iter += 1
        #
        if entireSet: entireSet = False #toggle entire set loop
        elif (alphaPairsChanged == 0): entireSet = True  
        print "iteration number: %d" % iter
    return oS.b,oS.alphas

def calcWs(alphas,dataArr,classLabels):
    X = mat(dataArr);
    labelMat = mat(classLabels).transpose()
    m,n = shape(X)
    w = zeros((n,1))
    for i in range(m):
        w += multiply(alphas[i]*labelMat[i],X[i,:].T)
    return w

def testRbf(k1=1.3):
    dataArr,labelArr = loadDataSet('testSetRBF.txt')
    b,alphas = smoP(dataArr, labelArr, 200, 0.0001, 10000, ('rbf', k1)) #C=200 important
    datMat=mat(dataArr); labelMat = mat(labelArr).transpose()
    svInd=nonzero(alphas.A>0)[0]
    sVs=datMat[svInd] #get matrix of only support vectors
    labelSV = labelMat[svInd];
    print "there are %d Support Vectors" % shape(sVs)[0]
    m,n = shape(datMat)
    errorCount = 0
    for i in range(m):
        kernelEval = kernelTrans(sVs,datMat[i,:],('rbf', k1))
        predict=kernelEval.T * multiply(labelSV,alphas[svInd]) + b
        if sign(predict)!=sign(labelArr[i]): errorCount += 1
    print "the training error rate is: %f" % (float(errorCount)/m)
    dataArr,labelArr = loadDataSet('testSetRBF2.txt')
    errorCount = 0
    datMat=mat(dataArr); labelMat = mat(labelArr).transpose()
    m,n = shape(datMat)
    for i in range(m):
        kernelEval = kernelTrans(sVs,datMat[i,:],('rbf', k1))
        predict=kernelEval.T * multiply(labelSV,alphas[svInd]) + b
        if sign(predict)!=sign(labelArr[i]): errorCount += 1    
    print "the test error rate is: %f" % (float(errorCount)/m)    
    
def img2vector(filename):
    returnVect = zeros((1,1024))
    fr = open(filename)
    for i in range(32):
        lineStr = fr.readline()
        for j in range(32):
            returnVect[0,32*i+j] = int(lineStr[j])
    return returnVect

''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

def loadImages(dirName):
    from os import listdir
    hwLabels = []
    trainingFileList = listdir(dirName)           #load the training set
    m = len(trainingFileList)
    trainingMat = zeros((m,1024))
    for i in range(m):
        fileNameStr = trainingFileList[i]
        fileStr = fileNameStr.split('.')[0]     #take off .txt
        classNumStr = int(fileStr.split('_')[0])
        if classNumStr == 9: hwLabels.append(-1)
        else: hwLabels.append(1)
        trainingMat[i,:] = img2vector('%s/%s' % (dirName, fileNameStr))
    return trainingMat, hwLabels    

def testDigits(kTup=('rbf', 10)):
    dataArr,labelArr = loadImages('trainingDigits')
    b,alphas = smoP(dataArr, labelArr, 200, 0.0001, 10000, kTup)
    datMat=mat(dataArr);
    labelMat = mat(labelArr).transpose()
    svInd=nonzero(alphas.A>0)[0]
    sVs=datMat[svInd] 
    labelSV = labelMat[svInd];
    print "there are %d Support Vectors" % shape(sVs)[0]
    m,n = shape(datMat)
    errorCount = 0
    for i in range(m):
        kernelEval = kernelTrans(sVs,datMat[i,:],kTup)
        predict=kernelEval.T * multiply(labelSV,alphas[svInd]) + b
        if sign(predict)!=sign(labelArr[i]): errorCount += 1
    print "the training error rate is: %f" % (float(errorCount)/m)
    dataArr,labelArr = loadImages('testDigits')
    errorCount = 0
    datMat=mat(dataArr); labelMat = mat(labelArr).transpose()
    m,n = shape(datMat)
    for i in range(m):
        kernelEval = kernelTrans(sVs,datMat[i,:],kTup)
        predict=kernelEval.T * multiply(labelSV,alphas[svInd]) + b
        if sign(predict)!=sign(labelArr[i]): errorCount += 1    
    print "the test error rate is: %f" % (float(errorCount)/m) 

