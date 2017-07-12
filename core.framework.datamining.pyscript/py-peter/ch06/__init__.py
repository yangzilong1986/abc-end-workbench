#coding=utf-8
import svmMLiA

from numpy import *

# dataAtrr,labelArr=svmMLiA.loadDataSet('testSet.txt')
# b,alphas=svmMLiA.smoP(dataAtrr,labelArr,0.6,0.001,40)
# b,alphas=svmMLiA.smoSimple(dataAtrr,labelArr,0.6,0.001,4)
# svmMLiA.testRbf()

dataAtrr,labelArr=svmMLiA.loadDataSet('testMult.txt')
dataMatrix = mat(dataAtrr);
labelMat = mat(labelArr).transpose()
label=mat(labelArr)
m,n = shape(dataMatrix)
#alphas估值矩阵[100,1]
alphas = mat([2,-1,1,1,1,1]).transpose()
result=multiply(alphas,labelMat)#100,1,向量积
print result
#<type 'list'>: [matrix([[ 1.]]), matrix([[ 1.]]), matrix([[ 1.]]), matrix([[ 1.]]), matrix([[ 1.]]), matrix([[ 1.]])]
#<type 'tuple'>: (6L, 1L)
# [[ 1.]
#  [ 1.]
#  [ 1.]
#  [ 1.]
#  [ 1.]
#  [ 1.]]

# [[-1.]
#  [ 2.]
#  [ 1.]
#  [ 1.]
#  [-1.]
#  [-1.]]
# <type 'tuple'>: (6L, 1L)
result2=alphas*label#100,1,向量积
print result2