#coding=utf-8

class treeNode:
    def __init__(self, nameValue, numOccur, parentNode):
        self.name = nameValue
        self.count = numOccur
        self.nodeLink = None
        #needs to be updated
        self.parent = parentNode
        self.children = {} 
    
    def inc(self, numOccur):
        self.count += numOccur
        
    def disp(self, ind=1):
        print '  '*ind, self.name, ' ', self.count
        for child in self.children.values():
            child.disp(ind+1)

#create FP-tree from dataset but don't mine
def createTree(dataSet, minSup=1):
    headerTable = {}
    #go over dataSet twice\
    #first pass counts frequency of occurance
    for trans in dataSet:#trans:frozenset(['e', 'm', 'q', 's', 't', 'y', 'x', 'z'])
        for item in trans:#item:'e'
            headerTable[item] = headerTable.get(item, 0) + dataSet[trans]
    #headerTable:{'e': 1, 'h': 1, 'j': 1, 'm': 1, 'o': 1, 'n': 1, 'q': 2, 'p': 2, 's': 3, 'r': 3, 'u': 1, 't': 3, 'w': 1, 'v': 1, 'y': 3, 'x': 4, 'z': 5}
    #remove items not meeting minSup,
    for k in headerTable.keys():
        if headerTable[k] < minSup: 
            del(headerTable[k])
    freqItemSet = set(headerTable.keys())#删除之后的headerTable为{'s': 3, 'r': 3, 't': 3, 'y': 3, 'x': 4, 'z': 5}
    #print 'freqItemSet: ',freqItemSet，freqItemSet值为set(['s', 'r', 't', 'y', 'x', 'z'])
    #if no items meet min support -->get out
    if len(freqItemSet) == 0:
        return None, None
    for k in headerTable:
        #reformat headerTable to use Node link
        headerTable[k] = [headerTable[k], None]#[]为list，seq

    #headerTable：{'s': [3, None], 'r': [3, None], 't': [3, None], 'y': [3, None], 'x': [4, None], 'z': [5, None]}
    #create tree，
    retTree = treeNode('Null Set', 1, None)

    #go through dataset 2nd time
    # dataSet.items()值为
    # 0 = {tuple} <type 'tuple'>: (frozenset(['e', 'm', 'q', 's', 't', 'y', 'x', 'z']), 1)
    # 1 = {tuple} <type 'tuple'>: (frozenset(['x', 's', 'r', 'o', 'n']), 1)
    # 2 = {tuple} <type 'tuple'>: (frozenset(['s', 'u', 't', 'w', 'v', 'y', 'x', 'z']), 1)
    # 3 = {tuple} <type 'tuple'>: (frozenset(['q', 'p', 'r', 't', 'y', 'x', 'z']), 1)
    # 4 = {tuple} <type 'tuple'>: (frozenset(['h', 'r', 'z', 'p', 'j']), 1)
    # 5 = {tuple} <type 'tuple'>: (frozenset(['z']), 1)
    for tranSet, count in dataSet.items():#tranSet：frozenset(['e', 'm', 'q', 's', 't', 'y', 'x', 'z'])
        localD = {}#localD：{'y': 3, 'x': 4, 's': 3, 'z': 5, 't': 3}
        for item in tranSet:  #put transaction items in order，item值为：e
            if item in freqItemSet:#freqItemSet：set(['s', 'r', 't', 'y', 'x', 'z'])
                localD[item] = headerTable[item][0]
        if len(localD) > 0:
            orderedItems = [v[0] for v in sorted(localD.items(),#localD
                                                 key=lambda p: p[1],
                                                 reverse=True)]
            #populate tree with ordered freq itemset，<type 'list'>: ['z', 'x', 'y', 's', 't']
            updateTree(orderedItems, retTree, headerTable, count)
    return retTree, headerTable #return tree and header table

def updateTree(items, inTree, headerTable, count):
    #check if orderedItems[0] in retTree.children，items值为：<type 'list'>: ['z', 'x', 'y', 's', 't']
    if items[0] in inTree.children:
        #incrament count inTree.children 'x' 'z' >，树节点计数增加
        inTree.children[items[0]].inc(count)#items值为<type 'list'>: ['x', 'y', 'r', 't'],items[0]:'x'
    else:   #add items[0] to inTree.children
        inTree.children[items[0]] = treeNode(items[0], count, inTree)
        #update header table #headerTable值为{'s': [3, None], 'r': [3, None], 't': [3, None], 'y': [3, None], 'x': [4, None], 'z': [5, None]}
        if headerTable[items[0]][1] == None:
            headerTable[items[0]][1] = inTree.children[items[0]]
        else:
            updateHeader(headerTable[items[0]][1], inTree.children[items[0]])
    #call updateTree() with remaining ordered items
    if len(items) > 1:#items[1::]为删除第一个节点：<type 'list'>: ['x', 'y', 's', 't']
        updateTree(items[1::], inTree.children[items[0]], headerTable, count)

#this version does not use recursion
def updateHeader(nodeToTest, targetNode):
    #Do not use recursion to traverse a linked list!
    while (nodeToTest.nodeLink != None):
        nodeToTest = nodeToTest.nodeLink
    nodeToTest.nodeLink = targetNode

#ascends from leaf node to root
def ascendTree(leafNode, prefixPath):
    if leafNode.parent != None:
        prefixPath.append(leafNode.name)
        ascendTree(leafNode.parent, prefixPath)

#treeNode comes from header table
def findPrefixPath(basePat, treeNode):
    condPats = {}
    while treeNode != None:
        prefixPath = []
        ascendTree(treeNode, prefixPath)
        if len(prefixPath) > 1: 
            condPats[frozenset(prefixPath[1:])] = treeNode.count
        treeNode = treeNode.nodeLink
    return condPats

def mineTree(inTree, headerTable, minSup, preFix, freqItemList):
    bigL = [v[0] for v in sorted(headerTable.items(),
                                 key=lambda p: p[1])]#(sort header table)
    for basePat in bigL:  #start from bottom of header table
        newFreqSet = preFix.copy()
        newFreqSet.add(basePat)
        #print 'finalFrequent Item: ',newFreqSet    #append to set
        freqItemList.append(newFreqSet)
        condPattBases = findPrefixPath(basePat, headerTable[basePat][1])
        #print 'condPattBases :',basePat, condPattBases
        #2. construct cond FP-tree from cond. pattern base
        myCondTree, myHead = createTree(condPattBases, minSup)
        #print 'head from conditional tree: ', myHead
        if myHead != None: #3. mine cond. FP-tree
            #print 'conditional tree for: ',newFreqSet
            #myCondTree.disp(1)            
            mineTree(myCondTree, myHead, minSup, newFreqSet, freqItemList)

def loadSimpDat():
    simpDat = [['r', 'z', 'h', 'j', 'p'],
               ['z', 'y', 'x', 'w', 'v', 'u', 't', 's'],
               ['z'],
               ['r', 'x', 'n', 'o', 's'],
               ['y', 'r', 'x', 'z', 'q', 't', 'p'],
               ['y', 'z', 'x', 'e', 'q', 's', 't', 'm']]
    return simpDat

def createInitSet(dataSet):
    retDict = {}
    for trans in dataSet:
        retDict[frozenset(trans)] = 1
    return retDict

# import twitter
from time import sleep
import re

def textParse(bigString):
    urlsRemoved = re.sub('(http:[/][/]|www.)([a-z]|[A-Z]|[0-9]|[/.]|[~])*', '', bigString)    
    listOfTokens = re.split(r'\W*', urlsRemoved)
    return [tok.lower() for tok in listOfTokens if len(tok) > 2]

def getLotsOfTweets(searchStr):
    CONSUMER_KEY = ''
    CONSUMER_SECRET = ''
    ACCESS_TOKEN_KEY = ''
    ACCESS_TOKEN_SECRET = ''
    # api = twitter.Api(consumer_key=CONSUMER_KEY, consumer_secret=CONSUMER_SECRET,
    #                   access_token_key=ACCESS_TOKEN_KEY,
    #                   access_token_secret=ACCESS_TOKEN_SECRET)
    #you can get 1500 results 15 pages * 100 per page
    resultsPages = []
    for i in range(1,15):
        print "fetching page %d" % i
        searchResults =''# api.GetSearch(searchStr, per_page=100, page=i)
        resultsPages.append(searchResults)
        sleep(6)
    return resultsPages

def mineTweets(tweetArr, minSup=5):
    parsedList = []
    for i in range(14):
        for j in range(100):
            parsedList.append(textParse(tweetArr[i][j].text))
    initSet = createInitSet(parsedList)
    myFPtree, myHeaderTab = createTree(initSet, minSup)
    myFreqList = []
    mineTree(myFPtree, myHeaderTab, minSup, set([]), myFreqList)
    return myFreqList

#minSup = 3
#simpDat = loadSimpDat()
#initSet = createInitSet(simpDat)
#myFPtree, myHeaderTab = createTree(initSet, minSup)
#myFPtree.disp()
#myFreqList = []
#mineTree(myFPtree, myHeaderTab, minSup, set([]), myFreqList)
