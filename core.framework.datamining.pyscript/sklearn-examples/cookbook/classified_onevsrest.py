#encoding:utf-8
from sklearn import datasets
import numpy as np
from sklearn import preprocessing
from sklearn import pipeline
from sklearn import decomposition
from matplotlib import pyplot as plt
from sklearn.datasets import load_boston
from sklearn.linear_model import LinearRegression
from scipy.stats import probplot
import operator
import pandas as pd
import itertools

from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import confusion_matrix
from sklearn.svm import SVC

X, y = datasets.make_classification(n_samples=10000, n_classes=3,n_informative=3)
from sklearn.tree import DecisionTreeClassifier
dt = DecisionTreeClassifier()
dt.fit(X, y)
preds=dt.predict(X)
print('preds')
print(preds)

from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
# 我们覆盖 LogisticRegression 分类器，同时，注意我们可以使其并行化。
# OneVsRestClassifier 分类器，它仅仅是训练单独的模型，之后比较它们。
# 所以，我们可以同时单独训练数据。

mlr = OneVsRestClassifier(LogisticRegression(), n_jobs=2)
mlr.fit(X, y)
mlr.predict(X)


