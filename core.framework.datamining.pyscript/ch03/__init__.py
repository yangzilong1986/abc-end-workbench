#coding=utf-8

import trees
import treePlotter

# def createDataSet():
#     dataSet = [[1,              1,          'yes'],
#                [1,              1,          'yes'],
#                [1,              0,          'no'],
#                [2,              0,          'maybe'],
#                [0,              1,           'no'],
#                [0,              1,           'no']]
#     labels = ['no surfacing','flippers']
#     #change to discrete values
#     return dataSet, labels

#dataSet, labels=createDataSet()
#fishMyTree=trees.createTree(dataSet,labels)

#print(fishMyTree)
# labels = ['no surfacing','flippers']
#fishClass=trees.classify(fishMyTree,labels,[0,2])
#print(fishClass)

#treePlotter.createPlot(fishMyTree)


#id3tree=trees.createTree(dataSet,labels)

#print(id3tree)

'''
决策树应用
'''
fr=open('lenses.txt')
lenses=[inst.strip().split('\t') for inst in fr.readlines()]
lensesLabels=['age' , 'presscript' , 'astigmatic', 'tearRate']
labels=['age' , 'presscript' , 'astigmatic', 'tearRate']
#{'tearRate': {'reduced': 'no lenses',
#               'normal': {'astigmatic': {'yes': {'presscript': {
#                                                       'hyper': {'age': {
#                                                                      'pre': 'no lenses',
#                                                                       'presbyopic': 'no lenses',
#                                                                       'young': 'hard'}
#                                                                    },
#                                                       'myope': 'hard'}},
#                                         'no': {'age': {'pre': 'soft',
#                                                       'presbyopic': {'presscript': {'hyper': 'soft',
#                                                                                   'myope': 'no lenses'}},
#                                                        'young': 'soft'}
# }}}}}

lensesTree=trees.createTree(lenses,labels)
print(lensesTree)
#young	hyper	no	normal	soft
# young	hyper	yes	reduced	no lenses
# young	hyper	yes	normal	hard
# pre	myope	no	reduced	no lenses
# pre	myope	no	normal	soft
lensesClass=trees.classify(lensesTree,lensesLabels,['pre','myope','no','reduced'])
print(lensesClass)

treePlotter.createPlot(lensesTree)