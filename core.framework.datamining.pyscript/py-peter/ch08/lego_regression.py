#coding=utf-8

from numpy import *
import regression
import readdata
def loadDataSet():

    numFeat = len(open(readdata.fileName).readline().split('\t'))-1
    dataMat = []; labelMat = []
    fr = open(readdata.fileName)
    for line in fr.readlines():
        lineArr =[]
        curLine = line.strip().split('\t')
        for i in range(numFeat):
            lineArr.append(float(curLine[i]))
        dataMat.append(lineArr)
        labelMat.append(float(curLine[-1]))
    return dataMat,labelMat

dataSet,labelMat=loadDataSet()

def legoLocalData():

    m,n =shape(dataSet)
    lgX=mat(ones((m,n+1)))
    lgX[:,1:n+1]=mat(dataSet)

    ws=regression.standRegres(lgX,labelMat)
    print ws
    return ws

def displayWeights(weights):
    import matplotlib.pyplot as plt
    fig=plt.figure()
    ax=fig.add_subplot(111)
    ax.plot(weights)
    plt.show()