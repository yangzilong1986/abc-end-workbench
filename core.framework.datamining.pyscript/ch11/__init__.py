#coding=utf-8
import apriori
from numpy import *
def loadDataSet():
    return [[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]

if __name__ == '__main__':

    datSet=loadDataSet()
    #[[1, 3, 4], [2, 3, 5], [1, 2, 3, 5], [2, 5]]
    datMat=mat(datSet)
    # print(datMat)
    #
    # Cl=apriori.createC1(datSet)
    # print("C1")
    # print(Cl)
    #
    # D=map(set,datSet)
    # #D[set([1, 3, 4]), set([2, 3, 5]), set([1, 2, 3, 5]), set([2, 5])]
    # L1,suppData0=apriori.scanD(D,Cl,0.5)
    # print("L1")
    # print(L1)

    L2,suppData0=apriori.apriori(datSet)
    print("L2")
    print(L2)