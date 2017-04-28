#coding=utf-8
import regTrees
from numpy import *

myDat=regTrees.loadDataSet('ex00.txt')
myMat=mat(myDat)
myTree=regTrees.createTree(myMat)
print(myTree)
