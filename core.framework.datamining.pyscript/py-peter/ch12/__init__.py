#coding=utf-8
import fpGrowth
from numpy import *
if __name__ == '__main__':
    rootNode=fpGrowth.treeNode('p',9,None)
    rootNode.children['eye']=fpGrowth.treeNode('eye',13,None)
    rootNode.disp()

    simpleData=fpGrowth.loadSimpDat()
    print(simpleData)

    initSet=fpGrowth.createInitSet(simpleData)
    print(initSet)

    myFPTree,myHeaderTab=fpGrowth.createTree(initSet,3)
    print("myFPTree")
    myFPTree.disp()
    print("myFPTree")
    print(myFPTree)