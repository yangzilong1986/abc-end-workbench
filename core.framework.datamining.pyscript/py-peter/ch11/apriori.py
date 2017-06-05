#coding=utf-8

from numpy import *

def createC1(dataSet):
    '''
    构建初始候选项集的列表，即所有候选项集只包含一个元素，
    C1是大小为1的所有候选项集的集合
    '''
    C1 = []
    for transaction in dataSet:
        for item in transaction:
            if not [item] in C1:
                C1.append([item])
    #C1:<type 'list'>: [[1], [2], [3], [4], [5]]
    C1.sort()
    #use frozen set so we
    #can use it as a key in a dict
    #dt <type 'list'>: [frozenset([1]), frozenset([2]),
    #                   frozenset([3]), frozenset([4]), frozenset([5])]
    dt=map(frozenset, C1)
    return dt

'''
D:数据集，样本
Ck:候选项列表
'''
def scanD(D, Ck, minSupport):
    '''
    计算Ck中的项集在数据集合D(记录或者transactions)中的支持度,
    返回满足最小支持度的项集的集合，和所有项集支持度信息的字典。
    '''
    #D:<type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
    #Ck:<type 'list'>: [frozenset([1]), frozenset([2]), frozenset([3]), frozenset([4]), frozenset([5])]
    ssCnt = {}#{ }字典Dictionary，[]列表List,()元组tuple
    for tid in D:#tid是每个数据集，D是训练数据集,tid:set([1, 3, 4])
        # 对于每一条transaction
        for can in Ck:#can:frozenset([1])
            # 对于每一个候选项集can，检查是否是transaction的一部分
            # 即该候选can是否得到transaction的支持
            #如果s是t的子集，则返回True,否则返回False
            if can.issubset(tid):
                if not ssCnt.has_key(can):
                    ssCnt[can]=1
                else:
                    ssCnt[can] += 1
    #样本集的条数
    numItems = float(len(D))
    retList = []
    supportData = {}
    for key in ssCnt:#样本属性
        # 每个项集的支持度
        support = ssCnt[key]/numItems
        # 将满足最小支持度的项集，加入retList
        if support >= minSupport:
            retList.insert(0,key)
        # 汇总支持度数据
        supportData[key] = support
     #supportData:
        # {frozenset([4]): 0.25, frozenset([5]): 0.75, frozenset([2]): 0.75, frozenset([3]): 0.75, frozenset([1]): 0.5}
        #{frozenset([1, 2]): 0.25, frozenset([1, 5]): 0.25, frozenset([3, 5]): 0.5,
        # frozenset([2, 3]): 0.5, frozenset([2, 5]): 0.75, frozenset([1, 3]): 0.5}
     #retList:
        # <type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
        # <type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
    return retList, supportData

###########################################################
########dataSet:样本数据集
########minSupport:支持度
###########################################################
def apriori(dataSet, minSupport = 0.5):
    #dataSet：<type 'list'>: [[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]
    # 构建初始候选项集C1
    #<type 'list'>: [frozenset([1]), frozenset([2]), frozenset([3]), frozenset([4]), frozenset([5])]
    C1 = createC1(dataSet)

    # 将dataSet集合化，以满足scanD的格式要求
    #<type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]

    #D:<type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
    D = map(set, dataSet)

    # 构建初始的频繁项集，即所有项集只有一个元素
    #L1:<type 'list'>: [[frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]]
    L1, supportData = scanD(D, C1, minSupport)
    L = [L1]

    # 最初的L1中的每个项集含有一个元素，新生成的
    # 项集应该含有2个元素，所以 k=2
    k = 2
    while (len(L[k-2]) > 0):#4
        #(L[k-2]:<type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
        Ck = aprioriGen(L[k-2], k)
        #扫描数据集，从Ck得到Lk,
        #D：<type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
        # Ck<type 'list'>:
        #  [frozenset([1, 3]), frozenset([1, 2]), frozenset([1, 5]),
        #  frozenset([2, 3]), frozenset([3, 5]), frozenset([2, 5])]
        Lk, supK = scanD(D, Ck, minSupport)#scan DB to get Lk

        # 将新的项集的支持度数据加入原来的总支持度字典中
        supportData.update(supK)
        # 将符合最小支持度要求的项集加入L
        L.append(Lk)
        # 新生成的项集中的元素个数应不断增加
        k += 1
    # 返回所有满足条件的频繁项集的列表，和所有候选项集的支持度信息
    return L, supportData

# Aprior算法
def aprioriGen(Lk, k): #creates Ck
    '''
   由初始候选项集的集合Lk生成新的生成候选项集，
   k表示生成的新项集中所含有的元素个数
   '''
    #Lk:<type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
    #k=2
    retList = []
    lenLk = len(Lk)
    for i in range(lenLk):
        #比较每一个
        for j in range(i+1, lenLk): 
            L1 = list(Lk[i])[:k-2]#list(Lk[i])值为1 <type 'list'>: [1, 3]  [:k-2]: [1] <type 'list'>: [2, 5]
            L2 = list(Lk[j])[:k-2] #<type 'list'>: [2, 3]
            L1.sort();
            L2.sort()
            if L1==L2: #if first k-2 elements are equal{}{}
                Lu=Lk[i] | Lk[j]
                retList.append(Lu) #set union
    #<type 'list'>:
    # [frozenset([1, 3]), frozenset([1, 2]), frozenset([1, 5]),
    # frozenset([2, 3]), frozenset([3, 5]), frozenset([2, 5])]

   # <type 'list'>: [frozenset([2, 3, 5])]
    return retList

#关联规则生成函数
def generateRules(L, supportData, minConf=0.7):  #supportData is a dict coming from scanD
    bigRuleList = []
    #only get the sets with two or more items
    #获取大于2个元素集合
    for i in range(1, len(L)):
        for freqSet in L[i]:
            H1 = [frozenset([item]) for item in freqSet]
            if (i > 1):
                rulesFromConseq(freqSet, H1, supportData, bigRuleList, minConf)
            else:
                calcConf(freqSet, H1, supportData, bigRuleList, minConf)
    return bigRuleList

# 规则生成与评价
def calcConf(freqSet, H, supportData, brl, minConf=0.7):
    '''
   计算规则的可信度，返回满足最小可信度的规则。
   freqSet(frozenset):频繁项集
   H(frozenset):频繁项集中所有的元素
   supportData(dic):频繁项集中所有元素的支持度
   brl(tuple):满足可信度条件的关联规则
   minConf(float):最小可信度
   '''

    prunedH = [] #create new list to return
    for conseq in H:
        conf = supportData[freqSet]/supportData[freqSet-conseq] #calc confidence
        if conf >= minConf: 
            print freqSet-conseq,'-->',conseq,'conf:',conf
            brl.append((freqSet-conseq, conseq, conf))
            prunedH.append(conseq)
    return prunedH

def rulesFromConseq(freqSet, H, supportData, brl, minConf=0.7):
    '''
   对频繁项集中元素超过2的项集进行合并。

   freqSet(frozenset):频繁项集
   H(frozenset):频繁项集中的所有元素，即可以出现在规则右部的元素
   supportData(dict):所有项集的支持度信息
   brl(tuple):生成的规则

   '''
    m = len(H[0])

    # 查看频繁项集是否大到移除大小为 m　的子集
    if (len(freqSet) > (m + 1)): #try further merging
        Hmp1 = aprioriGen(H, m+1)#create Hm+1 new candidates
        Hmp1 = calcConf(freqSet, Hmp1, supportData, brl, minConf)

        # 如果不止一条规则满足要求，进一步递归合并
        if (len(Hmp1) > 1):    #need at least two sets to merge
            rulesFromConseq(freqSet, Hmp1, supportData, brl, minConf)
