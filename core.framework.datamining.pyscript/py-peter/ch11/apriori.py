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
支持度计算
D:数据集，样本  <type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
Ck:候选项列表
 <type 'list'>: [frozenset([1, 3]), frozenset([1, 2]), frozenset([1, 5]),
                 frozenset([2, 3]), frozenset([3, 5]), frozenset([2, 5])]
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
        #<type 'list'>: [[frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])], [frozenset([1, 3]),
        # frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]]
        #L为list(list)的结构
        Ck = aprioriGen(L[k-2], k)
        #扫描数据集，从Ck得到Lk,
        #D：<type 'list'>: [set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
        # Ck<type 'list'>:
        #最后结果为
        # 0 = {list} <type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
        # 1 = {list} <type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
        # 2 = {list} <type 'list'>: [frozenset([2, 3, 5])]
        # 3 = {list} <type 'list'>: []
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
'''
输入参数：频繁项集列表Lk与项集元素个数k，输出为Ck
例如：以{0},{1},{2}作为输入则产生{0,1},{0,2},{1,2}

Python切片.
Python下标是以0开始的
x[1:3]表示返回集合中下标1至3（不包括3)的元素集合
x[:3] 表示返回从开始到下标3（不包括3）的元素集合
x[3:]表示返回从下标3到结束的元素集合
即返回从开始下标到结束下标之间的集合（不包括结束下标）
'''
def aprioriGen(Lk, k): #creates Ck
    '''
   由初始候选项集的集合Lk生成新的生成候选项集，
   k表示生成的新项集中所含有的元素个数
   '''
    #Lk:<type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
    #   <type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
    #k=2
    #首先创建一个空列表，然后计算频繁项集列表Lk的元素数目
    retList = []
    lenLk = len(Lk)
    #比较频繁项集列表Lk中的每个元素与其它元素，通过构建两个循环来实现
    #取列表中的两个集合进行比较，如果两个集合的前k-2元素都相等，则合并为一个
    for i in range(lenLk):#<type 'list'>: [0, 1, 2, 3]
        #比较每一个<type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
        # k为3时，频繁项集列表Lk：<type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
        for j in range(i+1, lenLk):#j从i的后一个元素开始<type 'list'>: [1, 2, 3]，<type 'list'>: [2, 3], <type 'list'>: [3]
            LS1=Lk[i]
            LS2=Lk[j]
            LL1=list(LS1)
            LL2=list(LS2)
            ##从0开始到k-2的元素，但是不包括k-2
            L1 =LL1[:k-2]#当k为2时，则L1为空，当k为3时
            L2 =LL2[:k-2]#当k为2时，则L1为空
            L1.sort()
            L2.sort()
            if L1==L2: #前k-2个相同，if first k-2 elements are equal{}{}
                Lu=Lk[i] | Lk[j]
                retList.append(Lu) #set union
            #<type 'list'>:
            # [frozenset([1, 3]), frozenset([1, 2]), frozenset([1, 5]),
            # frozenset([2, 3]), frozenset([3, 5]), frozenset([2, 5])]
            #<type 'list'>: [frozenset([1, 3]), frozenset([1, 2]), frozenset([1, 5]),
                # frozenset([2, 3]), frozenset([3, 5]), frozenset([2, 5])]
           # <type 'list'>: [frozenset([2, 3, 5])]
    return retList

#关联规则生成函数，频繁项集列表，L
#<type 'list'>:
# [[frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])],
# [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])], [frozenset([2, 3, 5])], []]

# 包含频繁项集支持数据的字典supportData
# {frozenset([5]): 0.75, frozenset([3]): 0.75,
#  frozenset([2, 3, 5]): 0.5, frozenset([1, 2]): 0.25, frozenset([1, 5]): 0.25,
#  frozenset([3, 5]): 0.5, frozenset([4]): 0.25, frozenset([2, 3]): 0.5,
#  frozenset([2, 5]): 0.75, frozenset([1]): 0.5, frozenset([1, 3]): 0.5, frozenset([2]): 0.75}
def generateRules(L, supportData, minConf=0.7):  #supportData is a dict coming from scanD
    bigRuleList = []#可信度的规则列表，基于可信度对它们进行排序
    #only get the sets with two or more items
    #获取大于2个元素集合
    #L <type 'list'>:
    # 0 = {list} <type 'list'>: [frozenset([1]), frozenset([3]), frozenset([2]), frozenset([5])]
    # 1 = {list} <type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
    # 2 = {list} <type 'list'>: [frozenset([2, 3, 5])]
    # 3 = {list} <type 'list'>: []
    for i in range(1, len(L)):#仅仅获取有两个以上的元素集合
        for freqSet in L[i]:#L[1]为<type 'list'>: [frozenset([1, 3]), frozenset([2, 5]), frozenset([2, 3]), frozenset([3, 5])]
            H1 = [frozenset([item]) for item in freqSet]
            if (i > 1):
                #当i大于等于2时进入此方法，进入时值如下：

                #bigRuleList值为：<type 'list'>: [
                # (frozenset([3]), frozenset([1]), 0.6666666666666666),
                # (frozenset([1]), frozenset([3]), 1.0),
                # (frozenset([5]), frozenset([2]), 1.0),
                #  (frozenset([2]), frozenset([5]), 1.0),
                # (frozenset([3]), frozenset([2]), 0.6666666666666666),
                #  (frozenset([2]), frozenset([3]), 0.6666666666666666),
                # (frozenset([5]), frozenset([3]), 0.6666666666666666),
                # (frozenset([3]), frozenset([5]), 0.6666666666666666)]

                #freqSet的值为frozenset([2, 3, 5])，H值为<type 'list'>: [frozenset([2]), frozenset([3]), frozenset([5])]
                rulesFromConseq(freqSet, H1, supportData, bigRuleList, minConf)
            else:
                calcConf(freqSet, H1, supportData, bigRuleList, minConf)
    return bigRuleList

# 规则生成与评价，项集中只包含两个元素，则计算可信度
#freqSet:frozenset([1, 3])
#H:<type 'list'>: [frozenset([1]), frozenset([3])]
#supportData:
# {frozenset([5]): 0.75, frozenset([3]): 0.75, frozenset([2, 3, 5]): 0.5, frozenset([1, 2]): 0.25,
# frozenset([1, 5]): 0.25, frozenset([3, 5]): 0.5, frozenset([4]): 0.25, frozenset([2, 3]): 0.5,
# frozenset([2, 5]): 0.75, frozenset([1]): 0.5, frozenset([1, 3]): 0.5, frozenset([2]): 0.75}
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
        #freqSet:frozenset([1, 3])
        #conseq:frozenset([1])
        #supportData[freqSet]:0.5
        #freqSet-conseq:frozenset([3])
        #supportData[freqSet-conseq]:0.75
        conf = supportData[freqSet]/supportData[freqSet-conseq] #calc confidence
        if conf >= minConf: 
            print freqSet-conseq,'-->',conseq,'conf:',conf
            brl.append((freqSet-conseq, conseq, conf))
            prunedH.append(conseq)
    return prunedH

#rulesFromConseq实现从最初的项集中生成更多的相关规则
#freqSet频繁项集
#H：可以出现在规则右部的元素列表
#freqSet：frozenset([2, 3, 5])
# H：<type 'list'>: [frozenset([2]), frozenset([3]), frozenset([5])]
#递归进入
# H:<type 'list'>: [frozenset([2, 3]), frozenset([2, 5]), frozenset([3, 5])]
def rulesFromConseq(freqSet, H, supportData, brl, minConf=0.7):
    '''
   对频繁项集中元素超过2的项集进行合并。
   freqSet(frozenset):频繁项集
   H(frozenset):频繁项集中的所有元素，即可以出现在规则右部的元素
   supportData(dict):所有项集的支持度信息
   brl(tuple):生成的规则
   '''
    m = len(H[0])#计算H中的频繁集大小

    # 查看频繁项集是否大到移除大小为 m的子集,
    #递归进入时m:2,len(freqSet)为3
    if (len(freqSet) > (m + 1)): #try further merging
        # H值为<type 'list'>: [frozenset([2]), frozenset([3]), frozenset([5])]
        #Hmp1值<type 'list'>: [frozenset([2, 3]), frozenset([2, 5]), frozenset([3, 5])]
        Hmp1 = aprioriGen(H, m+1)#create H m+1 new candidates，
        #calcConf可能修改可信度的规则列表brl
        Hmp1 = calcConf(freqSet, Hmp1, supportData, brl, minConf)
        # 如果不止一条规则满足要求，进一步递归合并
        if (len(Hmp1) > 1):    #need at least two sets to merge
            rulesFromConseq(freqSet, Hmp1, supportData, brl, minConf)
