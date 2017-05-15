#coding=utf-8

from numpy import *
import operator
from os import listdir

def createDataSet():
    group = array(
        [[1.0,1.1],
         [1.0,1.0],
         [0,  0],
         [0,  0.1]
         ])
    labels = ['A','A','B','B']
    return group, labels

'''
intX:分类的输入向量
dataSet:样本数据
标签
k:最近邻居的数目

样例
cf=kNN.classify0([0,0],group,labels,1)

kNN分类算法描述
    对未知类别数据集中的每个元素依次执行如下操作
    计算已知类别数据集中的点与当前点之间的距离
    按照距离递增次序排序
    选取与当前距离最小的k个点
    确定前k个点在所有列表出现的频率
    返回当前k个点出现频率最高的列表作为当前点的预测分类
'''
def classify0(inX, dataSet, labels, k):
    ###############################################
    #数据集行数即数据集记录数,数组的大小可以通过其shape属性获得，行和列
    #shape的应用示例
    # c.shape = 4,3
    # array([[ 1,  2,  3],
    #        [ 4,  4,  5],
    #        [ 6,  7,  7],
    #        [ 8,  9, 10]])
    ###############################################

    ################################################
    #计算欧拉距离
    ################################################
    #获取样本数据的行数
    #在列方向上重复inX[0,0]1次，行dataSetSize次
    dataSetSize = dataSet.shape[0]#数组的维度

    #获取矩阵的差值
    ###############################################
    #计算已知类别数据集中的点与当前点之间的距离
    ###############################################
    #((A0-B0)**2+(A1-B1)**2)**0.5
    # dataSet结构
    # [[ 1.   1.1]
    #  [ 1.   1. ]
    # [ 0.   0. ]
    # [ 0.   0.1]]

    #初始化List[]
    #tile([0,0],(1,3))#在列方向上重复[0,0]3次，行1次
    # datTile=tile(inX, (dataSetSize,3))
    # 0 = {ndarray} [0 0 0 0 0 0]
    # 1 = {ndarray} [0 0 0 0 0 0]
    # 2 = {ndarray} [0 0 0 0 0 0]
    # 3 = {ndarray} [0 0 0 0 0 0]

    #重复中心数据inX，取样本数据行数据和1创建数组
    datTile=tile(inX, (dataSetSize,1))
    # datTile结构
    # [
    # [0 0]
    #  [0 0]
    # [0 0]
    # [0 0]
    # ]

    #样本与原先所有样本的差值矩阵，标准样本和测试样本矩阵 两个矩阵的元素的差
    diffMat = datTile - dataSet
    # diffMat = tile(inX, (dataSetSize,1)) - dataSet
    #样本数据集
    # diffMat结果
    # [[-1.  -1.1]
    #  [-1.  -1. ]
    #  [ 0.   0. ]
    # [ 0.  -0.1]]

    #差值取平方,矩阵
    sqDiffMat = diffMat**2
    #sqDiffMat结果
    # [[ 1.    1.21]
    #  [ 1.    1.  ]
    # [ 0.    0.  ]
    # [ 0.    0.01]]

    # a = np.array([
    #     [1,2,3],
    #     [4,5,6]
    # ])
    # print(a.sum())           # 对整个矩阵求和
    # # 结果 21
    #
    # print(a.sum(axis=0)) # 对行方向求和
    # # 结果 [5 7 9]
    #
    # print(a.sum(axis=1)) # 对列方向求和
    # # 结果 [ 6 15]

    #列加到一起，
    sqDistances = sqDiffMat.sum(axis=1)
    #对列方向求和，[ 2.21  2. 0. 0.01]
    #sqDiffMat结果       sqDistances
    # [[ 1.    1.21]        2.21
    #  [ 1.    1.  ]        2.
    # [ 0.    0.  ]         0.
    # [ 0.    0.01]]        0.01
    distances = sqDistances**0.5
    #distances：<type 'list'>: [1.4866068747318506, 1.4142135623730951, 0.0, 0.10000000000000001]
    #sqrtMatrix = [0-> [0: 1.4866068747318506] [1: 1.4142135623730951] [2: 0.0] [3: 0.1] ]
###############################################
    #按照距离递增次序排序

    #按照值，返回排序索引,即 对距离大小进行排序
    sortedDistIndicies = distances.argsort()
    #sortedDistIndicies <type 'list'>: [2, 3, 1, 0]

    # 注：tuple（元祖） 用小括号，
    # Dictionary (字典) : 用{}来定义，
    # list（列表） 用方括号[]
    ###############################################
    classCount={}#{'A':1,'B':1 },

    #选取与当前距离最小的k个点，k为传入参数

    #投票表决
    #即选择距离最小的 K 个点
    for i in range(k):
        #labels为传入值A A B B
        #当前k个点出现频率最高的列表作为当前点的预测分类
        voteIlabel = labels[sortedDistIndicies[i]]
        #classCount值为{'B': 1}
        classCount[voteIlabel] = classCount.get(voteIlabel,0) + 1

    # 按照类别的数量多少进行排序
    #确定前k个点在所有列表出现的频率
    sortedClassCount = sorted(classCount.iteritems(),
                              key=operator.itemgetter(1),
                              reverse=True)

    sortedRow=sortedClassCount[0][0]
    # 返回类别数最多的类别名称
    return sortedRow



