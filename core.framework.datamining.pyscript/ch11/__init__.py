#coding=utf-8
import apriori
from numpy import *
if __name__ == '__main__':
    datSet=apriori.loadDataSet()

    # datMat=mat(datSet)
    # print(datMat)
    #
    # Cl=apriori.createC1(datSet)
    # print(Cl)
    #
    # D=map(set,datSet)
    # print(D)
    #
    # L1,suppData0=apriori.scanD(D,Cl,0.5)
    # print(L1)

    L1,suppData0=apriori.apriori(datSet)
    print(L1)