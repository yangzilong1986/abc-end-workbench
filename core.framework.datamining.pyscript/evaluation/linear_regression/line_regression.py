# -*- coding: utf-8 -*-
"""
最小二乘法直接计算方式
在实践中， β0。和β1 都是未知的。所以在用式(3. 1) 做出预测之前，我们必须根据数据集
估计系数。令
(x1， y1),(x2， y2),(x3， y3),...,(xn， yn)

"""
from numpy import *
import numpy as np 
import operator
import matplotlib.pyplot as plt 

def loadDataSet(fileName): 
    X = []; Y = []
    fr = open(fileName)
    for line in fr.readlines():
        curLine = line.strip().split('\t')
        X.append(float(curLine[0])); Y.append(float(curLine[-1]))
    return X,Y

#绘制散点
def plotscatter(Xmat,Ymat,plt):
	# fig=plt.subplot()
	# fig = plt.figure()
	ax = plt.subplot(2,1,1)
	# ax = fig.add_subplot(111)  # 绘制图形位置
	plt.plot(Xmat,Ymat, 'go'  ,markersize=2)	# 绘制散点图


def plotLine(Xmat,yhat,plt):
	plt.plot(Xmat,yhat,'r') # 绘制回归线

#根据回归的系数计算预测值
def cal(Xmat,Ymat,a,b):
	x=Xmat
	# x.sort() # 对Xmat各元素进行排序
	yhat = [a*float(xi)+b for xi in x] # 计算预测值
	return yhat

#拟合
def leastSquare(Xmat, Ymat):
	meanX = mean(Xmat) # 原始数据集的均值
	meanY = mean(Ymat)

	dX = Xmat-meanX  # 各元素与均值的差
	dY = Ymat-meanY

	sumXY = vdot(dX,dY)    # 返回两个向量的点乘 multiply
	SqX = sum(power(dX,2)) # 向量的平方：(X-meanX)^2

	# 计算斜率和截距
	a = sumXY/SqX
	b = meanY - a*meanX
	return a,b



#yArr and yHatArr both need to be arrays
def rssError(yArr,yHatArr):
	return ((yArr-yHatArr)**2).sum()

#拟合一个变量
def leastSquareDedail(Xmat, Ymat):
	meanX = mean(Xmat) # 原始数据集的均值
	meanY = mean(Ymat)

	dX = Xmat-meanX  # 各元素与均值的差
	dY = Ymat-meanY
	# m,n=shape(Xmat)
	ws =[]
	# 手工计算：
	sumXY = 0; SqX = 0
	for i in xrange(len(dX)):
		sumXY += double(dX[i])*double(dY[i])
		SqX += double(dX[i])**2
		ws.append(SqX)

	# 计算斜率和截距
	a = sumXY/SqX
	b = meanY - a*meanX
	return a,b

def pltDot(xDot,yDot,rg,plt):
	ax = plt.subplot(2,1,2)
	plt.tight_layout(pad=0.4, w_pad=0.5, h_pad=1.0)
	plt.plot(xDot, yDot, 'ro',markersize=5)
	plt.axis(rg)

def plt3d(X, Y, Z):
	from mpl_toolkits.mplot3d import Axes3D
	fig = plt.figure(1)

	ax = Axes3D(fig)


	ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap=plt.cm.hot)
	ax.contourf(X, Y, Z, zdir='z', offset=-2, cmap=plt.cm.hot)
	ax.set_zlim(-2,2)

# 数据文件名
Xmat, Ymat= loadDataSet("D:/DevN/sample-data/zhengjie-data/chapter07/regdataset.txt")
plt.figure(figsize=(10, 8))
a,b=leastSquare(Xmat, Ymat)
yhat=cal(Xmat,Ymat,a,b)

#详细拟合过程
leastSquareDedail(Xmat, Ymat)

# 绘制图形
plotscatter(Xmat,Ymat,plt)
plotLine(Xmat,yhat,plt)
xDot=[1,2,3,4]
yDot=[1,4,9,16]

rg=[0, max(xDot)+1,0, max(yDot)+1]
pltDot(xDot,yDot,rg,plt)

plt.show()