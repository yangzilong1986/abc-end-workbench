#coding=utf-8

from numpy import *

from time import sleep
from votesmart import votesmart

def pntRules(ruleList, itemMeaning):

    for ruleTup in ruleList:
        for item in ruleTup[0]:
            print itemMeaning[item]
        print "           -------->"
        for item in ruleTup[1]:
            print itemMeaning[item]
        print "confidence: %f" % ruleTup[2]
        print       #print a blank line


def getBillApi(billNum):
    # 12939	12940	12830	12857	12988	12040	12465	11451
    # 11364	11820	12452	11318	11414	11719	11205	12747
    # 12792	12827	12445	12049
    billId={}
    billId[12939] =  ['1', '2', '3']
    billId[12940] =  ['1', '2', '3']
    billId[12830] =  ['4', '15', '16']
    billId[12857] =  ['7', '18', '19']
    billId[12988] =  ['1', '12', '13']
    billId[12465] =  ['6', '18', '19','11']
    billId[11451] =  ['1', '17', '18','12']
    billId[11364] =  ['2', '11', '15']
    billId[11820] =  ['11', '22', '23']
    billId[12452] =  ['11', '32', '33']
    billId[12940] =  ['21', '22', '33']
    billId[11318] =  ['31', '22', '13']
    billId[11414] =  ['11', '32', '18']
    billId[11719] =  ['11', '32', '18']
    billId[11205] =  ['11', '22', '19']
    billId[12747] =  ['11', '22', '14']
    billId[12792] =  ['11', '12', '16']
    billId[12827] =  ['11', '12', '15']
    billId[12445] =  ['11', '12', '18']
    billId[12049] =  ['11', '12', '19']
    billId[12040] =  ['11', '12', '19']

    return billId.pop(billNum)

#votesmart.apikey = 'get your api key first'
def getActionIds():
    actionIdList = []; billTitleList = []
    fr = open('recent20bills.txt')
    for line in fr.readlines():
        billNum = int(line.split('\t')[0])
        try:
            actions = getBillApi(billNum) #api call
            for action in actions:
                    actionId = int(action)
                    # print 'bill: %d has actionId: %d' % (billNum, actionId)
                    actionIdList.append(actionId)
                    billTitleList.append(line.strip().split('\t')[1])
        except:
            print "problem getting bill %d" % billNum
        # sleep(1)                                      #delay to be polite
    return actionIdList, billTitleList

class Vote:
    def __str__(self):
        return ': '.join((self.officeParties, self.action))
    def __init__(self,office,act,cname="Hello"):
       self.candidateName=cname
       self.officeParties=office
       self.action=act

def  getBillActionVotes(actionId):
    # [1, 2, 3, 21, 22, 33, 4, 15, 16, 7, 18, 19, 1, 12, 13, 11, 12, 19, 6, 18,
    # 19, 11, 1, 17, 18, 12, 2, 11, 15,
    # 11, 22, 23, 11, 32, 33, 31, 22, 13, 11, 32, 18, 11, 32, 18, 11, 22, 19, 11, 22,
    # 14, 11, 12, 16, 11, 12, 15, 11, 12, 18, 11, 12, 19]
    vote=Vote("","Yea")
    votes={}
    votes[1]=[Vote("Democratic","","Dolye"),Vote("Republican","","Mike"),Vote("Republican","Nay","Micket"),Vote("Republican","Yea","Bytee")]
    votes[2]=[Vote("Democratic","","Dolye"),Vote("Democratic","Nay","Mike"),Vote("Democratic","Yea","Micket")]
    votes[3]=[Vote("Republican","","")]
    votes[4]=[Vote("","Nay","")]

    votes[5]=[Vote("Democratic","",""),Vote("Republican","",""),Vote("","Nay",""),Vote("","Yea,""")]
    votes[6]=[Vote("Democratic","","")]
    votes[7]=[Vote("Republican","","")]
    votes[8]=[Vote("","Nay","")]
    votes[9]=[Vote("Democratic","","")]
    votes[7]=[Vote("Republican","","")]

    votes[11]=[Vote("Democratic","",""),Vote("Republican","",""),Vote("","Nay",""),Vote("","Yea","")]
    votes[12]=[Vote("Democratic","","")]
    votes[13]=[Vote("Republican","","")]
    votes[14]=[Vote("","Nay")]

    votes[15]=[Vote("Democratic","",""),Vote("Republican","",""),Vote("","Nay",""),Vote("","Yea","")]
    votes[16]=[Vote("Democratic","")]
    votes[17]=[Vote("Republican","")]
    votes[18]=[Vote("","Nay")]
    votes[19]=[Vote("Democratic","")]

    votes[21]=[Vote("Democratic",""),Vote("Republican",""),Vote("","Nay"),Vote("","Yea")]
    votes[22]=[Vote("Democratic","")]
    votes[23]=[Vote("Republican","")]
    votes[24]=[Vote("","Nay")]

    votes[25]=[Vote("Democratic",""),Vote("Republican",""),Vote("","Nay"),Vote("","Yea")]
    votes[26]=[Vote("Democratic","")]
    votes[27]=[Vote("Republican","")]
    votes[28]=[Vote("","Nay")]
    votes[29]=[Vote("Democratic","")]
    votes[27]=[Vote("Republican","")]

    votes[31]=[Vote("Democratic",""),Vote("Republican",""),Vote("","Nay"),Vote("","Yea")]
    votes[32]=[Vote("Democratic","")]
    votes[33]=[Vote("Republican","")]
    votes[34]=[Vote("","Nay")]

    votes[35]=[Vote("Democratic",""),Vote("Republican",""),Vote("","Nay"),Vote("","Yea")]
    votes[36]=[Vote("Democratic","")]
    votes[37]=[Vote("Republican","")]
    votes[38]=[Vote("","Nay")]
    votes[39]=[Vote("Democratic","")]
    return votes.pop(actionId)


def getTransList(actionIdList, billTitleList): #this will return a list of lists containing ints
    itemMeaning = ['Republican', 'Democratic']#list of what each item stands for
    for billTitle in billTitleList:#fill up itemMeaning list
        itemMeaning.append('%s -- Nay' % billTitle)
        itemMeaning.append('%s -- Yea' % billTitle)
    transDict = {}#list of items in each transaction (politician)
    voteCount = 2
    for actionId in actionIdList:

        # print 'getting votes for actionId: %d' % actionId
        try:
            voteList = getBillActionVotes(actionId)
            for vote in voteList:
                if not transDict.has_key(vote.candidateName):
                    transDict[vote.candidateName] = []
                    if vote.officeParties == 'Democratic':
                        transDict[vote.candidateName].append(1)
                    elif vote.officeParties == 'Republican':
                        transDict[vote.candidateName].append(0)
                if vote.action == 'Nay':
                    transDict[vote.candidateName].append(voteCount)
                elif vote.action == 'Yea':
                    transDict[vote.candidateName].append(voteCount + 1)
        except:
            print "problem getting actionId: %d" % actionId
        voteCount += 2
    return transDict, itemMeaning
