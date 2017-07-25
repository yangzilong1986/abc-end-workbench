#coding=utf-8
import svmMLiA

from numpy import *

# dataAtrr,labelArr=svmMLiA.loadDataSet('testSet.txt')
# b,alphas=svmMLiA.smoP(dataAtrr,labelArr,0.6,0.001,40)
# b,alphas=svmMLiA.smoSimple(dataAtrr,labelArr,0.6,0.001,4)
# svmMLiA.testRbf()

al=array([[1,2,3],[4,5,6]])
a2=array([[0.3,0.2,0.3],[0.4,0.5,0.6]])

mm=mat(al)
ss=mat(a2)

mul1=multiply(mm,ss)
print ('##########mm,ss##############')
print (mul1)

mul1=multiply(ss.T,mm.T)
print ('###########ss.T,mm.T#############')
print (mul1)

# mul1=multiply(ss,mm.T)
# print ('###########ss.T,mmt#############')
# print (mul1)

mat1=ss.T*mm
print ('############ss.T*mm############')
print (mat1)

mat2=mm*ss.T
print ('############mm*ss.T############')
print (mat2)
