#coding=utf-8

from math import log
import operator


'''
给定数据的香农熵，信息增益
'''
def calcShannonEnt(dataSet):
    #dataSet:<type 'list'>: [[1, 1, 'yes'], [1, 1, 'yes'], [1, 0, 'no'], [0, 1, 'no'], [0, 1, 'no']]
    #numEntries:5
    numEntries = len(dataSet)
    labelCounts = {}#{'yes': 2, 'no': 3}
    for featVec in dataSet: #the the number of unique elements and their occurance
        currentLabel = featVec[-1]#[1, 1, 'yes'], currentLabel为也是
        if currentLabel not in labelCounts.keys(): labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    shannonEnt = 0.0
    for key in labelCounts:
        #获取标签的概率
        prob = float(labelCounts[key])/numEntries
        #log base 2
        #香农熵
        #prob小于1，则shannonEnt为负值
        shannonEnt -= prob * log(prob,2)
    return shannonEnt

'''
划分数据集
dataSet:待划分的数据集
axis:划分数据集的特征
value:需要返回的数据值
'''
def splitDataSet(dataSet, axis, value):
    retDataSet = []#创建新的list对象
    for featVec in dataSet:#每行数据
        if featVec[axis] == value:#计算每列值：<type 'list'>: [['myope', 'no', 'reduced', 'no lenses']]
            #axis位置之后的值
            reducedFeatVec = featVec[:axis]     #chop out axis used for splitting
            #之前的数据
            reducedFeatVec.extend(featVec[axis+1:])
            retDataSet.append(reducedFeatVec)
    return retDataSet
    
def chooseBestFeatureToSplit(dataSet):
    #dataSet:<type 'list'>: [[1, 1, 'yes'], [1, 1, 'yes'], [1, 0, 'no'], [0, 1, 'no'], [0, 1, 'no']]
    #第一行
    ds=dataSet[0]
    numFeatures = len(ds) - 1      #the last column is used for the labels,numFeatures为2
    baseEntropy = calcShannonEnt(dataSet)
    bestInfoGain = 0.0; bestFeature = -1
    for i in range(numFeatures): #iterate over all the features
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
        # dataSet = [[1,              1,          'yes'],
        #            [1,              1,          'yes'],
        #            [1,              0,          'no'],
        #            [2,              0,          'maybe'],
        #            [0,              1,           'no'],
        #            [0,              1,           'no']]
        #第一次遍历，i为0，获取第一列
        #<type 'list'>: [1, 1, 1, 2, 0, 0]
        #第二次遍历，i为0，获取第二列
        #<type 'list'>: [1, 1, 0, 0, 1, 1]
        featList = [example[i] for example in dataSet]
        #去重
        uniqueVals = set(featList)       #get a set of unique values
        newEntropy = 0.0#
        for value in uniqueVals:
            #获取第i列并且值为value一节（行数）段数据
            #subDataSet:<type 'list'>: [[1, 'no'], [1, 'no']]
            subDataSet = splitDataSet(dataSet, i, value)
            prob = len(subDataSet)/float(len(dataSet))#prob:2/6
            newEntropy += prob * calcShannonEnt(subDataSet)
        #calculate the info gain; ie reduction in entropy
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
        if vote not in classCount.keys(): classCount[vote] = 0
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sortedClassCount[0][0]

'''
创建决策树
'''
def createTree(dataSet,labels):
    classList = [example[-1] for example in dataSet]
    if classList.count(classList[0]) == len(classList): #当列表中包含第一个元素的个数等于List的长度时，则为同一个元素
        return classList[0]#stop splitting when all of the classes are equal
    if len(dataSet[0]) == 1: #stop splitting when there are no more features in dataSet
        return majorityCnt(classList)
    #遍历完所有特征时返回出现次数最多的类别
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
        subLabels = labels[:]  #copy all of labels, so trees don't mess up existing labels
        myTree[bestFeatLabel][value] = createTree(splitDataSet(dataSet, bestFeat, value),subLabels)
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
    
