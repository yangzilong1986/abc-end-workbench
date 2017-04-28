#coding=utf-8
import adaboost
from numpy import *
# datMat,classLabels=adaboost.loadSimpData()
#
# D=mat(ones((5,1))/5)
# bStump=adaboost.buildStump(datMat,classLabels,D)
#
# classifierArray=adaboost.adaBoostTrainDS(datMat,classLabels,9)
#
# print('classifierArray')
# print(classifierArray)

datArr,labelArr=adaboost.loadDataSet('horseColicTraining2.txt')

classifierArray,aggClassEst=adaboost.adaBoostTrainDS(datArr,labelArr)

adaboost.plotROC(aggClassEst.T,labelArr)