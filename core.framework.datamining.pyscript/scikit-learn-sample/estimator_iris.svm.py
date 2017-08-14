#!/usr/bin/python
# -*- coding: utf-8 -*-
import numpy as np
from sklearn import cross_validation
from sklearn import datasets
from sklearn import svm
iris = datasets.load_iris()

print  iris.data.shape, iris.target.shape

# 可以快速的采样到一个训练集合同时保留 40% 的数据用于测试（评估）我们的分类器):
X_train, X_test, y_train, y_test = cross_validation.train_test_split(iris.data, iris.target,
                                   test_size=0.4, random_state=0)

print X_train.shape
print y_train.shape

clf = svm.SVC(kernel='linear', C=1).fit(X_train, y_train)
print  clf.score(X_test, y_test)

# 下面的例子演示了如何评估一个线性支持向量机在 iris 数据集上的精度，通过划分数据，
# 可以连续5次,使用不同的划分方法 拟合并评分
scores = cross_validation.cross_val_score(clf, iris.data, iris.target, cv=5)
print('连续五次的结果')
print(scores)