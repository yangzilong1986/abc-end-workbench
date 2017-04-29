#coding=utf-8
from  regTrees import *
from numpy import *

myDat=loadDataSet('ex00.txt')
myMat=mat(myDat)
myTree=createTree(myMat)
print(myTree)
#
# trainMat = mat(loadDataSet("bikeSpeedVsIq_train.txt"))
# testMat  = mat(loadDataSet("bikeSpeedVsIq_test.txt"))
# myTree   = createTree(trainMat, ops=(1,20))
# yHat     = createForeCast(myTree, testMat[:,0])
# print "回归树的皮尔逊相关系数：",corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]
#
#
# myTree   = createTree(trainMat, modelLeaf, modelErr,(1,20))
# yHat     = createForeCast(myTree, testMat[:,0], modelTreeEval)
# print "模型树的皮尔逊相关系数：",corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]
#
# ws, X, Y = linearSolve(trainMat)
# print "线性回归系数：",ws
# for i in range(shape(testMat)[0]):
#     yHat[i] = testMat[i,0]*ws[1,0] + ws[0,0]
# print "线性回归模型的皮尔逊相关系数：",corrcoef(yHat, testMat[:,1], rowvar=0)[0,1]