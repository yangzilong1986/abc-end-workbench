#coding=utf-8
import logRegres
from numpy import *

weights = ones((3,1))
print (weights)

dataArr,labelMat=logRegres.loadDataSet()

# dataMatrix = mat(dataArr) #convert to NumPy matrix
# print (dataMatrix)

# ascentMatrix=logRegres.gradAscent(dataArr,labelMat)
# print(ascentMatrix)
# logRegres.plotBestFit(ascentMatrix.getA())

# ascentMatrix=logRegres.stocGradAscent(array(dataArr),labelMat)
# print(ascentMatrix)
# logRegres.plotBestFit(ascentMatrix)

logRegres.multiTest()