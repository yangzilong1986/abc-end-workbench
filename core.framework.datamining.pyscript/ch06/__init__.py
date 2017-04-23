#coding=utf-8
import svmMLiA
dataAtrr,labelArr=svmMLiA.loadDataSet('testSet.txt')
b,alphas=svmMLiA.smoP(dataAtrr,labelArr,0.6,0.001,40)
