#coding=utf-8
import svmMLiA

from numpy import *

dataAtrr,labelArr=svmMLiA.loadDataSet('testSet.txt')
# b,alphas=svmMLiA.smoP(dataAtrr,labelArr,0.6,0.001,40)
b,alphas=svmMLiA.smoSimple(dataAtrr,labelArr,0.6,0.001,4)
# svmMLiA.testRbf()