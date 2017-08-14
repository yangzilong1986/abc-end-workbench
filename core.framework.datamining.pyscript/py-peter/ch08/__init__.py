#coding=utf-8
import regression
import lego_regression
from numpy import *
import matplotlib.pyplot as plt

#标准回归测试
def dispStandRegres():
    xArr,yArr=regression.loadDataSet('ex0.txt')
    ws=regression.standRegres(xArr,yArr)
    print(ws)

    xMat=mat(xArr)
    yMat=mat(yArr)

    yHat=xMat*ws

    import matplotlib.pyplot as plt

    fig=plt.figure()
    ax=fig.add_subplot(111)

    ax.scatter(xMat[:,1].flatten().A[0],
               yMat.T[:,0].flatten().A[0])

    xCopy=xMat.copy();
    xCopy.sort(0)
    yHat=xCopy*ws

    ax.plot(xCopy[:,1],yHat)

    #显示相关性
    coef_ = corrcoef( yHat.T ,yMat)
    plt.plot(1,  label="coef %s"%coef_)
    plt.title("coef %s"%coef_)

    plt.show()

# dispStandRegres()


xArr,yArr=regression.loadDataSet('ex0.txt')
# xArr,yArr=regression.loadDataSet('abalone.txt')

# ws=regression.lwlr(xArr[0],xArr,yArr,1.0)
def tunLWLRData(r=0.01):
    yHat=regression.lwlrTest(xArr,xArr,yArr,r)
    xMat=mat(xArr)

    srtInd=xMat[:,1].argsort(0)
    xSort=xMat[srtInd][:,0,:]

    yHatSort=yHat[srtInd]
    return yHat,xMat,xSort,yHatSort

def displayLWLR(r=0.01):
    import matplotlib.pyplot as plt

    f, ax = plt.subplots(figsize=(10, 8),nrows=3)

    yHat,xMat,xSort,yHatSort=tunLWLRData(r=1)

    ax[0].scatter(xMat[:,1].flatten().A[0],
                  mat(yArr).T[:,0].flatten().A[0],s=2,c='red')

    ax[0].plot(xSort[:,1],yHatSort)

    coef_ = corrcoef( yHat.T ,yArr)
    # plt.plot(1,  label="coef %s"%coef_)
    print ("coef %s"%coef_)
    ax[0].set_title('lwlr,k=1')

    yHat1,xMat1,xSort1,yHatSort1=tunLWLRData(r=0.01)
    ax[1].scatter(xMat1[:,1].flatten().A[0],mat(yArr).T[:,0].flatten().A[0],s=2,c='b')
    ax[1].plot(xSort1[:,1],yHatSort1)


    coef1_ = corrcoef( yHat1.T ,yArr)
    # plt.plot(1,  label="coef %s"%coef_)
    print ("coef %s"%coef1_)
    ax[0].set_title('lwlr,k=1')
    ax[1].set_title("lwlr,k=0.01")

    yHat2,xMat2,xSort2,yHatSort2=tunLWLRData(r=0.003)
    ax[2].scatter(xMat2[:,1].flatten().A[0],mat(yArr).T[:,0].flatten().A[0],s=2,c='g')
    ax[2].plot(xSort2[:,1],yHatSort2)
    ax[2].set_title("lwlr,k=0.003")
    coef2_ = corrcoef( yHat2.T ,yArr)
    # plt.plot(1,  label="coef %s"%coef_)
    print ("coef %s"%coef2_)
    plt.show()

def lwlrTest(testArr,xArr,yArr,k=1.0):
    m = shape(testArr)[0]
    ws =[]
    for i in range(m):
        ws_rw=regression.lwlrWeight(testArr[i], xArr, yArr, k)
        ws.append(ws_rw.A.flatten())
    return array(ws)

def displayLwlrWeight(k=1):
    import matplotlib.pyplot as plt
    f, ax = plt.subplots(figsize=(10, 8),nrows=3)

    xMat=mat(xArr)
    yMat=mat(yArr)

    ws=lwlrTest(xArr,xArr,yArr,1)
    # wsd=regression.lwlr_weight(xArr,xArr,yArr,r)
    #Minitab的散点图Scatter Plot功能，绘出回归线后
    # ax[0].scatter(xMat[:,1].flatten().A[0],ws[:,0],c='g')
    # lego_regression.displayWeights(ws)
    ax[0].plot(ws)

    # ax[1].scatter(xMat[:,1].flatten().A[0],ws[:,1],c='r')

    ws=lwlrTest(xArr,xArr,yArr,0.01)
    # wsd=regression.lwlr_weight(xArr,xArr,yArr,r)
    #Minitab的散点图Scatter Plot功能，绘出回归线后
    # ax[2].scatter(xMat[:,1].flatten().A[0],ws[:,0],c='g')
    #
    # ax[3].scatter(xMat[:,1].flatten().A[0],ws[:,1],c='r')

    ax[1].plot(ws)

    ws=lwlrTest(xArr,xArr,yArr,0.003)
    ax[2].plot(ws)
    # wsd=regression.lwlr_weight(xArr,xArr,yArr,r)
    #Minitab的散点图Scatter Plot功能，绘出回归线后
    # ax[4].scatter(xMat[:,1].flatten().A[0],ws[:,0],c='g')
    #
    # ax[5].scatter(xMat[:,1].flatten().A[0],ws[:,1],c='r')
    # lego_regression.displayWeights(ws)
    plt.show()

def displayRidge():
    xArr,yArr=regression.loadDataSet('abalone.txt')
    weights=regression.ridge(xArr, yArr)
    lego_regression.displayWeights(weights)


def abalone():
    xArr,yArr=regression.loadDataSet('abalone.txt')

    f, ax = plt.subplots(figsize=(10, 8),nrows=3)
    ws=regression.stageWise(xArr,yArr)
    ax[0].plot(ws)
    # eps=0.01,numIt=100
    # ws=regression.stageWise(xArr,yArr,eps=0.005,numIt=5000)
    # ax[1].plot(ws)
    #
    # ws=regression.stageWise(xArr,yArr,eps=0.0001,numIt=50000)
    # ax[2].plot(ws)
    plt.show()

# displayRidge()

# displayLWLR()

# displayLwlrWeight()

abalone()

# lego_regression.legoLocalData()