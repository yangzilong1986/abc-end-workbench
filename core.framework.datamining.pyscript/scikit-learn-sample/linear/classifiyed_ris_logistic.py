# -*- coding: utf-8 -*-
# 一对所有（one-Versus-All，OVA），给定m个类，训练m个二元分类器
# 即：将选取任意一类，再将其它所有类看成是一类，构建一个两类分类器。

# 分类器j使类j的元组为正类，其余为负类，进行训练。

# 为了对未知元组X进行分类，分类器作为一个组合分类器投票。
# 例如，如果分类器j预测X为正类，则类j得到一票。
# 如果他测得X为正类，则类j得到一票。如果测X为负类，则除j以外的每一个类都得到一票（相当于此类的票数减一）。得票最多的指派给X。
# 这种方法简单有效，而且使用类似logistic这种有概率值大小可以比较的情况下，类边界其实是个有范围的值，可以增加正确率。
# 而且当K(类别数量)很大时，通过投票的方式解决了一部分不平衡性问题。

from logisticRegression import *
from numpy import *
import operator

#知道了Iris共有三种类别Iris-setosa，Iris-versicolor和Iris-virginica
def loadDataSet(filename):
    numFeat = len(open(filename).readline().split(','))-1
    dataMat = []; labelMat = []
    fr = open(filename)
    for line in fr.readlines():
        lineArr = []
        curLine = line.strip().split(',')
        for i in range(numFeat):
            lineArr.append(float(curLine[i]))
        dataMat.append([1]+lineArr)  #这里是为了使 x0 等于 1
        labelMat.append(curLine[-1])
    return dataMat,labelMat

# voteResult = {'Iris-setosa':0,'Iris-versicolo':0,'Iris-virginica':0}#记录投票情况
voteResult = [0,0,0]
categorylabels = ['Iris-setosa','Iris-versicolor','Iris-virginica']#类别标签
opts = {'alpha': 0.01, 'maxIter': 100, 'optimizeType': 'smoothStocGradDescent'}
#训练过程
dataMat,labelMat = loadDataSet('train.txt')

weight1 = []
for i in range(3):#三类
    labelMat1 = []
    for j in range(len(labelMat)):#把名称变成0或1的数字
        if labelMat[j] == categorylabels[i]:
            labelMat1.append(1)
        else:
            labelMat1.append(0)

    dataMat = mat(dataMat);
    labelMat1 = mat(labelMat1).T
    # 给定m个类，训练m个二元分类器
    # 将选取任意一类，再将其它所有类看成是一类，构建一个两类分类器。
    weight1.append(logisticRegression(dataMat,labelMat1,opts))

#测试过程
dataMat,labelMat = loadDataSet('test.txt')
dataMat = mat(dataMat)

initial_value = 0
list_length = len(labelMat)
h = [initial_value]*list_length


for j in range(len(labelMat)):
    voteResult = [0,0,0]
    # 为了对未知元组X进行分类，分类器作为一个组合分类器投票。
    # 例如，如果分类器j预测X为正类，则类j得到一票。
    # 如果他测得X为正类，则类j得到一票。如果测X为负类，则除j以外的每一个类都得到一票（相当于此类的票数减一）。得票最多的指派给X。
    for i in range(3):
        h[j] = float(sigmoid(dataMat[j]*weight1[i]))#得到训练结果
        if h[j] > 0.5 and h[j] <= 1:
            voteResult[i] = voteResult[i]+1+h[j]#由于类别少，为了防止同票，投票数要加上概率值
        elif h[j] >= 0 and h[j] <= 0.5:
            voteResult[i] = voteResult[i]-1+h[j]
        else:
            print('Properbility wrong！')
    h[j] = voteResult.index(max(voteResult))



labelMat2 = []
for j in range(len(labelMat)):#把名称变成0或1或2的数字
    for i in range(3):#三类
        if labelMat[j] == categorylabels[i]:
            labelMat2.append(i);break

        #计算正确率
error = 0.0
for j in range(len(labelMat)):
    if h[j] != labelMat2[j]:
        error = error +1

pro = 1 - error / len(labelMat)#正确率
print pro




