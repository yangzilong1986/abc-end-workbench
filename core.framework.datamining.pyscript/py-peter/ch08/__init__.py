#coding=utf-8
import regression
from numpy import *

xArr,yArr=regression.loadDataSet('ex0.txt')
print(xArr[0:2])

# ws=regression.standRegres(xArr,yArr)
# print(ws)
#
# xMat=mat(xArr)
# yMat=mat(yArr)
#
# yHat=xMat*ws
#
# import matplotlib.pyplot as plt
#
# fig=plt.figure()
# ax=fig.add_subplot(111)
#
# ax.scatter(xMat[:,1].flatten().A[0],yMat.T[:,0].flatten().A[0])
#
# xCopy=xMat.copy();
# xCopy.sort(0)
# yHat=xCopy*ws
#
# ax.plot(xCopy[:,1],yHat)
# plt.show()


ws=regression.lwlr(xArr[0],xArr,yArr,1.0)
print(ws)


yHat=regression.lwlrTest(xArr,xArr,yArr,0.03)
xMat=mat(xArr)
srtInd=xMat[:,1].argsort(0)
xSort=xMat[srtInd][:,0,:]

import matplotlib.pyplot as plt

fig=plt.figure()
ax=fig.add_subplot(111)

ax.scatter(xMat[:,1].flatten().A[0],mat(yArr).T[:,0].flatten().A[0],s=2,c='red')

ax.plot(xSort[:,1],yHat[srtInd])
plt.show()