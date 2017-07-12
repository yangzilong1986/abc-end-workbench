#coding=utf-8
import apriori
from numpy import *
import  recentAprioriTest

def loadDataSet():
    return [[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]

if __name__ == '__main__':

    # datSet=loadDataSet()
    # #[[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]
    # datMat=mat(datSet)
    # # print(datMat)
    # #
    # Cl=apriori.createC1(datSet)
    # print("C1")
    # print(Cl)
    # #
    # D=map(set,datSet)
    # #D[set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
    # L1,suppData0=apriori.scanD(D,Cl,0.5)
    # #retList, supportData
    # print("retList-L1")
    # print(L1)
    # print("supportData-suppData0")
    # print(suppData0)
    # # apriori.aprioriGen()
    # L2,suppData0=apriori.apriori(datSet)
    # print("L2")
    # print(L2)
    # print("suppData0")
    # print(suppData0)
    # #
    # rules=apriori.generateRules(L2,suppData0,minConf=0.6)
    # print("rules")
    # print(rules)

    actionIdList, billTitleList=recentAprioriTest.getActionIds()
    # print("actionIdList")
    # print(actionIdList)
    # print("billTitleList")
    # print(billTitleList)

    transDict, itemMeaning=recentAprioriTest.getTransList(actionIdList[:2],billTitleList[:2])
    # print("transDict")
    # print(transDict)
    # print("transDict-keys")
    #
    #
    # print("itemMeaning")
    # print(itemMeaning)

    dataSet=[transDict[key] for key in transDict.keys()]
    # print("itemMeaning-dataSet")
    # print(dataSet)

    L,suppData=apriori.apriori(dataSet,minSupport=0.2)
    print("L")
    print(L)
    print("suppData")
    print(suppData)

    rules=apriori.generateRules(L,suppData,minConf=0.9)
    print("rules")
    print(rules)