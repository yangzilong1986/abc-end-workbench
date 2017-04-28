#coding=utf-8

from numpy import *

#
def createVocabList(dataSet):
    #去重List
    #使用set创建不重复词表库
    vocabSet = set([])  #create empty set
    for document in dataSet:
        #创建两个集合的并集
        vocabSet = vocabSet | set(document) #union of the two sets
    return list(vocabSet)

def setOfWords2Vec(vocabList, inputSet):
    #创建一个所包含元素都为0的向量
    returnVec = [0]*len(vocabList)
    #遍历文档中的所有单词，如果出现了词汇表中的单词，
    # 则将输出的文档向量中的对应值设为1
    for word in inputSet:
        if word in vocabList:
            vec=vocabList.index(word)
            returnVec[vec] = 1
        else: print "the word: %s is not in my Vocabulary!" % word
    return returnVec

'''
将每个词的出现与否作为一个特征，这可以被描述为词集模型(set-of-words model)。
如果一个词在文档中出现不止一次，这可能意味着包含该词是否出现在文档中所不能表达的某种信息,
这种方法被称为词袋模型(bag-of-words model)。
在词袋中，每个单词可以出现多次，而在词集中，每个词只能出现一次。
'''
def bagOfWords2VecMN(vocabList, inputSet):
    returnVec = [0]*len(vocabList)
    for word in inputSet:
        if word in vocabList:
            returnVec[vocabList.index(word)] += 1
    return returnVec

'''
朴素贝叶斯分类器训练函数
trainMatrix类型为ndarray,shape为tuple(6,32)
每个标本的条件概率
'''
def trainNB0(trainMatrix,trainCategory):
    '''
    朴素贝叶斯分类器训练函数(此处仅处理两类分类问题)
    trainMatrix:文档矩阵
    trainCategory:每篇文档类别标签
    '''
    numTrainDocs = len(trainMatrix)#numTrainDocs:6
    numWords = len(trainMatrix[0]) #numWords:32
    pAbusive = sum(trainCategory)/float(numTrainDocs)

    p0Num = ones(numWords)

    #numWords（32）是一个List
    #change to ones()
    p1Num = ones(numWords)

    p0Denom = 2.0;
    p1Denom = 2.0  #change to 2.0
    #对每篇训练文章
    for i in range(numTrainDocs):#行
        #分类
        pos=trainMatrix[i]
        if trainCategory[i] == 1:#训练级
            #同一个元素的个数，p1Num为一行32个元素，
            #p1Num
            # <type 'list'>: [1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0,
            # 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0,
            #  1.0, 1.0, 1.0, 1.0, 2.0]
            p1Num +=pos#列相同
            p1Denom += sum(pos)#总和
        else:
            p0Num += pos#行加
            p0Denom += sum(pos)
    #用对数，是容错机制
    #将结果取自然对数，避免下溢出，即太多很小的数相乘造成的影响
    #每个单词为1的计数
    #p1Denom单词为1的总和
    p1Vect = log(p1Num/p1Denom)#每个单词出现的概率
    p0Vect = log(p0Num/p0Denom)#每个单词出现的概率
    return p0Vect,p1Vect,pAbusive

#分类函数
'''
vec2Classify:ndarray
    shape(tuple(32L,)
p0Vec:ndarray
    shape(tuple(32L,)
'''
def classifyNB(vec2Classify, p0Vec, p1Vec, pClass1):
    '''
    分类函数
    vec2Classify:要分类的向量
    p0Vec, p1Vec, pClass1:分别对应trainNB0计算得到的3个概率
    '''
    p1 = sum(vec2Classify * p1Vec) + log(pClass1) #element-wise mult
    p0 = sum(vec2Classify * p0Vec) + log(1.0 - pClass1)
    if p1 > p0:
        return 1
    else: 
        return 0

def textParse(bigString):    #input is big string, #output is word list
    import re
    listOfTokens = re.split(r'\W*', bigString)
    return [tok.lower() for tok in listOfTokens if len(tok) > 2] 

###############################################################################
########################默认测试方法###########################################
def loadDataSet():
    #postingList: 进行词条切分后的文档集合
    #classVec:类别标签
    postingList=[['my',    'dog',       'has',      'flea',          'problems',    'help',  'please'],#0-0
                 ['maybe', 'not',       'take',      'him',          'to',           'dog',     'park',      'stupid'],#1-1
                 ['my',    'dalmation',  'is',       'so',           'cute',         'I',       'love',     'him'],#2-0
                 ['stop',    'posting',  'stupid',   'worthless',   'garbage'],#3-1
                 ['mr',      'licks',       'ate',       'my',         'steak',
                  'how',   'to',        'stop',     'him'],#4-0
                 ['quit',   'buying',    'worthless', 'dog',         'food',             'stupid'],#5-1
         ]#6
    classVec = [0,1,0,1,0,1]    #1 is abusive, 0 not.7行
    return postingList,classVec

def testingNB():
    #样本数据
    listOPosts,listClasses = loadDataSet()

    #非重复单词列表,32
    myVocabList = createVocabList(listOPosts)
    trainMat=[]
    for postinDoc in listOPosts:#listOPosts原文档相邻
        trainMat.append(setOfWords2Vec(myVocabList, postinDoc))

    #训练结果
    p0V,p1V,pAb = trainNB0(array(trainMat),array(listClasses))

    #测试数据

    testEntry = ['love', 'my', 'dalmation']
    thisDoc = array(setOfWords2Vec(myVocabList, testEntry))
    #thisDoc值为
    #[0 1 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1]
    print testEntry,'classified as: ',classifyNB(thisDoc,p0V,p1V,pAb)

    testEntry = ['stupid', 'garbage']
    thisDoc = array(setOfWords2Vec(myVocabList, testEntry))
    print testEntry,'classified as: ',classifyNB(thisDoc,p0V,p1V,pAb)

#邮件分类
def spamTest():
    docList=[]; classList = []; fullText =[]
    for i in range(1,26):
        wordList = textParse(open('email/spam/%d.txt' % i).read())
        #源类型加入
        #  ['a', 'b', 'c', 'd', 'e', 'f', ['d', 'e', 'f']])
        docList.append(wordList)
        #源类型只能是List[]
        #list=['a','b','c']
        #list.extend(['d','e','f'])
        #结果为
        #['a', 'b', 'c', 'd', 'e', 'f'])
        fullText.extend(wordList)
        #设置垃圾邮件类标签为1
        classList.append(1)
        wordList = textParse(open('email/ham/%d.txt' % i).read())
        docList.append(wordList)
        fullText.extend(wordList)
        classList.append(0)

    #生成次表库
    vocabList = createVocabList(docList)#create vocabulary
    trainingSet = range(50);
    testSet=[]           #create test set
    #随机选10组做测试集
    for i in range(10):#测试集
        randIndex = int(random.uniform(0,len(trainingSet)))
        testSet.append(trainingSet[randIndex])
        del(trainingSet[randIndex])

    trainMat=[]; trainClasses = []
    #生成训练矩阵及标签
    for docIndex in trainingSet:#train the classifier (get probs) trainNB0
        trainMat.append(bagOfWords2VecMN(vocabList, docList[docIndex]))
        trainClasses.append(classList[docIndex])
    p0V,p1V,pSpam = trainNB0(array(trainMat),array(trainClasses))
    errorCount = 0

    #测试并计算错误率
    for docIndex in testSet:        #classify the remaining items
        wordVector = bagOfWords2VecMN(vocabList, docList[docIndex])
        if classifyNB(array(wordVector),p0V,p1V,pSpam) != classList[docIndex]:
            errorCount += 1
            print "classification error",docList[docIndex]
    print 'the error rate is: ',float(errorCount)/len(testSet)
    #return vocabList,fullText

#######################################################
'''
返回前30个高频词
 '''
def calcMostFreq(vocabList,fullText):
    import operator
    freqDict = {}
    for token in vocabList:
        freqDict[token]=fullText.count(token)
    sortedFreq = sorted(freqDict.iteritems(),
                        key=operator.itemgetter(1),
                        reverse=True)
    return sortedFreq[:30]       


'''
函数localWords()与程序清单中的spamTest()函数几乎相同，区别在于这里访问的是
RSS源而不是文件。然后调用函数calcMostFreq()来获得排序
最高的30个单词并随后将它们移除
'''
def localWords(feed1,feed0):
    import feedparser
    docList=[];
    classList = [];
    fullText =[]
    minLen = min(len(feed1['entries']),len(feed0['entries']))
    for i in range(minLen):
        wordList = textParse(feed1['entries'][i]['summary'])
        docList.append(wordList)
        fullText.extend(wordList)
        classList.append(1) #NY is class 1
        wordList = textParse(feed0['entries'][i]['summary'])
        docList.append(wordList)
        fullText.extend(wordList)
        classList.append(0)

    #生成次表库
    vocabList = createVocabList(docList)#create vocabulary
    top30Words = calcMostFreq(vocabList,fullText)   #remove top 30 words
    for pairW in top30Words:
        if pairW[0] in vocabList: vocabList.remove(pairW[0])
    trainingSet = range(2*minLen); testSet=[]#create test set
    for i in range(20):
        randIndex = int(random.uniform(0,len(trainingSet)))
        testSet.append(trainingSet[randIndex])
        del(trainingSet[randIndex])  
    trainMat=[]; trainClasses = []

    #train the classifier (get probs) trainNB0
    for docIndex in trainingSet:
        trainMat.append(bagOfWords2VecMN(vocabList, docList[docIndex]))
        trainClasses.append(classList[docIndex])

    p0V,p1V,pSpam = trainNB0(array(trainMat),array(trainClasses))
    errorCount = 0
    #classify the remaining items
    for docIndex in testSet:
        wordVector = bagOfWords2VecMN(vocabList, docList[docIndex])
        if classifyNB(array(wordVector),p0V,p1V,pSpam) != classList[docIndex]:
            errorCount += 1
    print 'the error rate is: ',float(errorCount)/len(testSet)
    return vocabList,p0V,p1V

def getTopWords(ny,sf):
    import operator
    vocabList,p0V,p1V=localWords(ny,sf)
    topNY=[]; topSF=[]
    for i in range(len(p0V)):
        if p0V[i] > -6.0 : topSF.append((vocabList[i],p0V[i]))
        if p1V[i] > -6.0 : topNY.append((vocabList[i],p1V[i]))

    sortedSF = sorted(topSF, key=lambda pair: pair[1], reverse=True)
    print "SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**SF**"
    for item in sortedSF:
        print item[0]
    sortedNY = sorted(topNY, key=lambda pair: pair[1], reverse=True)
    print "NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**NY**"
    for item in sortedNY:
        print item[0]
