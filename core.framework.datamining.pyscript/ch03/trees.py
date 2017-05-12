#coding=utf-8

from math import log
import operator


'''
决策树的一般流程
    收集数据
    数据准备，树构造算法只适用于标称数据，需要对数值型数据进行离散化
    分析数据，
    训练算法，构造树的数据结构
    测试算法
    使用算法
'''

#给定数据的香农熵，信息增益
def calcShannonEnt(dataSet):
    #dataSet:<type 'list'>: [[1, 1, 'yes'], [1, 1, 'yes'], [1, 0, 'no'], [0, 1, 'no'], [0, 1, 'no']]
    #样本增量，numEntries:5
    numEntries = len(dataSet)
    #{'yes': 2, 'no': 3}
    labelCounts = {}
    #the the number of unique elements and their occurance
    for featVec in dataSet:
        #[1, 1, 'yes'],
        # currentLabel为数据的最后一行，取值为yes/no
        currentLabel = featVec[-1]
        if currentLabel not in labelCounts.keys():
            #设置分类的默认值
            labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1

    shannonEnt = 0.0
    #每个分类的计算，即计算每个分类的概率
    #labelCounts:{'no lenses': 5, 'hard': 1, 'soft': 2}
    for key in labelCounts:
        #获取标签的概率p(i|t)
        prob = float(labelCounts[key])/numEntries
        #log base 2
        #香农熵
        #prob小于1，则shannonEnt为负值
        shannonEnt -= prob * log(prob,2)
    return shannonEnt

'''
划分数据集
dataSet:待划分的数据集
axis:划分数据集的特征，取哪一列数据作为分类数据
value:分类
'''
def splitDataSet(dataSet, axis, value):
    retDataSet = []#创建新的list对象
    #计算每列值：<type 'list'>: [['myope', 'no', 'reduced', 'no lenses']]
    for featVec in dataSet:#每行数据
        if featVec[axis] == value:
            #axis位置之后的值
            #featVec：<type 'list'>: ['pre', 'myope', 'no', 'normal', 'soft']
            #featVec: <type 'list'>: ['young', 'hyper', 'no', 'reduced', 'no lenses']
            #过滤到axis行形成新的数据
            reducedFeatVec = featVec[:axis]#当获取样本第axis列时，获取其前面数据
            #位置之后的数据<type 'list'>: ['myope', 'no', 'normal', 'soft']
            reducedFeatVec.extend(featVec[axis+1:])
            #append保持元数据类型即List类型加入到List中
            retDataSet.append(reducedFeatVec)
    return retDataSet

#选择最好的数据分类划分方式
def chooseBestFeatureToSplit(dataSet):
    #dataSet:<type 'list'>: [[1, 1, 'yes'], [1, 1, 'yes'], [1, 0, 'no'], [0, 1, 'no'], [0, 1, 'no']]
    #第一行
    ds=dataSet[0]
    #the last column is used for the labels,
    #最后一行为标签
    # numFeatures为2
    numFeatures = len(ds) - 1

    #计算原始的香农熵
    baseEntropy = calcShannonEnt(dataSet)
    bestInfoGain = 0.0
    bestFeature = -1
    #iterate over all the features
    #遍历每个特征值
    for i in range(numFeatures):
        #example:<type 'list'>: [0, 1, 'no']        <type 'list'>: [0, 1, 'no']
        #featList:<type 'list'>: [1, 1, 1, 0, 0]    <type 'list'>: [1, 1, 0, 1, 1]
        #类似还有生成表达式
        #如featList = (example[i] for example in dataSet)
        #生成表达式一般可省略两端原括号，写成
        #featList = example[i] for example in dataSet
        #生成表达式和 for + yield 相似，
        #遍历数据集dataSet,获取dataSet的第1和第二列
        #第i列的数据
        #create a list of all the examples of this feature

        #遍历dataSet每行数据，每次遍历获取第i个，即获取一列数据
        # dataSet = [
        #            [1,              1,          'yes'],
        #            [1,              1,          'yes'],
        #            [1,              0,          'no'],
        #            [2,              0,          'maybe'],
        #            [0,              1,           'no'],
        #            [0,              1,           'no']
        # ]
        #第一次遍历，i为0，获取第一列
        #<type 'list'>: [1, 1, 1, 2, 0, 0]
        #第二次遍历，i为0，获取第二列
        #<type 'list'>: [1, 1, 0, 0, 1, 1]
        #在训练样本中遍历每一行，并取第i列作为特征值
        featList = [example[i] for example in dataSet]
        #去重
        #get a set of unique values
        #对一列样本集的分类
        uniqueVals = set(featList)
        newEntropy = 0.0
        for value in uniqueVals:
            #获取第i列并且值为value一节（行数）段数据
            #C4.5算法
            #选择A作为分裂点，在A下的分裂增益率
            # SplitInfo-A=Dj/D*Info(Dj)

            #subDataSet:<type 'list'>: [[1, 'no'], [1, 'no']]
            #i表示为第几列数据，value为分类
            #
            subDataSet = splitDataSet(dataSet, i, value)
            #在某子分类，获取某个分类的增益率
            prob = len(subDataSet)/float(len(dataSet))#prob:2/6 每个
            #信息增益
            # Info(Dj)=pilog(pi)
            newEntropy += prob * calcShannonEnt(subDataSet)
        #calculate the info gain; ie reduction in entropy
        #信息增益
        infoGain = baseEntropy - newEntropy
        #compare this to the best gain so far
        if (infoGain > bestInfoGain):
            #if better than current best, set to best
            bestInfoGain = infoGain
            bestFeature = i#新增获得增广，位置后移
    return bestFeature #returns an integer，bestFeature为0

'''
多数表决
类标签不唯一时，如何确定叶子节点的方法
'''
def majorityCnt(classList):
    classCount={}
    for vote in classList:
        if vote not in classCount.keys():
            classCount[vote] = 0
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.iteritems(),
                              key=operator.itemgetter(1), reverse=True)
    return sortedClassCount[0][0]

'''
创建决策树
'''
def createTree(dataSet,labels):
    # dataSet数据
    # young	myope	no	reduced	no lenses
    # young	myope	no	normal	soft
    # young	myope	yes	reduced	no lenses

    # labels
    # labels=['age' , 'presscript' , 'astigmatic', 'tearRate']
    #获取分类信息
    classList = [example[-1] for example in dataSet]

    #类别完全相同时则停止继续划分
    #当列表中包含第一个元素的个数等于List的长度时，则为同一个元素
    if classList.count(classList[0]) == len(classList):
        #stop splitting when all of the classes are equal
        return classList[0]

    #stop splitting when there are no more features in dataSet
    #遍历完所有特征时返回出现次数最多的类别
    if len(dataSet[0]) == 1:
        return majorityCnt(classList)

    #遍历完所有特征时返回出现次数最多的类别
    #选择对一个划分
    bestFeat = chooseBestFeatureToSplit(dataSet)

    bestFeatLabel = labels[bestFeat]
    myTree = {bestFeatLabel:{}}
    #del删除第bestFeat个元素
    del(labels[bestFeat])
    #得到列表包含的所有属性值
    featValues = [example[bestFeat] for example in dataSet]
    uniqueVals = set(featValues)#set唯一化
    for value in uniqueVals:
        #复制类标签，并将其存储在新的列表遍历subLabels
        #在PYTHON中函数参数是列表时，参数是按照引用方式传递的。
        #为了保证每次调用createTree时不改变原始列表的内容，因此使用新的变量代替原始列表
        #copy all of labels, so trees don't mess up existing labels
        subLabels = labels[:]
        retDataSet=splitDataSet(dataSet, bestFeat, value)
        myTree[bestFeatLabel][value] = createTree(retDataSet,subLabels)
    return myTree                            

'''
使用决策树的分类函数
'''
def classify(inputTree,featLabels,testVec):
    firstStr = inputTree.keys()[0]
    secondDict = inputTree[firstStr]
    featIndex = featLabels.index(firstStr)
    key = testVec[featIndex]
    valueOfFeat = secondDict[key]
    if isinstance(valueOfFeat, dict): 
        classLabel = classify(valueOfFeat, featLabels, testVec)
    else: classLabel = valueOfFeat
    return classLabel

def storeTree(inputTree,filename):
    import pickle
    fw = open(filename,'w')
    pickle.dump(inputTree,fw)
    fw.close()
    
def grabTree(filename):
    import pickle
    fr = open(filename)
    return pickle.load(fr)
    
