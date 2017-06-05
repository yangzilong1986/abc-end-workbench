#coding=utf-8
from bayes import *
import feedparser

testingNB()

# list=['a','b','c']
# list.extend(['d','e','f'])
# print('list.extend\t',list)
# list.append(['d','e','f'])
# print('list.extend append\t',list)

# spamTest()
#导入RSS数据源
# import operator
ny=feedparser.parse('http://newyork.craigslist.org/stp/index.rss')
sf=feedparser.parse('http://sfbay.craigslist.org/stp/index.rss')
getTopWords(ny,sf)
# vocabList,p0V,p1V=localWords(ny,sf)
# print(vocabList)