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

X, y = datasets.make_classification()
base_svm = SVC()
base_svm.fit(X, y)

#参数
# C ：以防我们的数据集不是分离好的， C 会在间距上放大误差。随着 C 变大，误差的惩罚也会变大，
# SVM 会尝试寻找一个更窄的间隔，即使它错误分类了更多数据点。

# class_weight ：这个表示问题中的每个类应该给予多少权重。
# 这个选项以字典提供，其中类是键，值是与这些类关联的权重。

# gamma ：这是用于核的 Gamma 参数，并且由 rgb, sigmoid 和 ploy 支持。

# kernel ：这是所用的核，我们在下面使用 linear 核，但是 rgb 更流行，并且是默认选项。
print('base_svm')
print(base_svm)

X, y = datasets.make_blobs(n_features=2, centers=2)
from sklearn.svm import LinearSVC
svm = LinearSVC()
svm.fit(X, y)
print('LinearSVC')
print(svm)

# 既然我们训练了支持向量机，我们将图中每个点结果绘制出来。这会向我们展示近似的决策边界。
from collections import namedtuple
from itertools import product

Point = namedtuple('Point', ['x', 'y', 'outcome'])
decision_boundary = []
xmin, xmax = np.percentile(X[:, 0], [0, 100])
ymin, ymax = np.percentile(X[:, 1], [0, 100])
for xpt, ypt in product(np.linspace(xmin-2.5, xmax+2.5, 20),
                        np.linspace(ymin-2.5, ymax+2.5, 20)):
    p = Point(xpt, ypt, svm.predict([xpt, ypt]))
    decision_boundary.append(p)

# 让我们看看其他例子，但是这一次决策边界不是那么清晰：
X_common, y__common = datasets.make_classification(n_features=2,
                                    n_classes=2,
                                    n_informative=2,
                                    n_redundant=0)
svm_common = LinearSVC()
svm_common.fit(X_common, y__common)
xmin_common, xmax_common = np.percentile(X_common[:, 0], [0, 100])
ymin_common, ymax_common = np.percentile(X_common[:, 1], [0, 100])
test_points = np.array([[xx, yy] for xx, yy in
                        product(np.linspace(xmin_common, xmax_common),
                                np.linspace(ymin_common, ymax_common))])

test_preds = svm_common.predict(test_points)

#
radial_svm = SVC(kernel='rbf')
radial_svm.fit(X, y)
xmin, xmax = np.percentile(X[:, 0], [0, 100])
ymin, ymax = np.percentile(X[:, 1], [0, 100])
test_points_rbf = np.array([[xx, yy] for xx, yy in
                        product(np.linspace(xmin, xmax),
                                np.linspace(ymin, ymax))])
test_preds_rbf = radial_svm.predict(test_points_rbf)

f, ax = plt.subplots(figsize=(10, 8),nrows=3)
colors = np.array(['r', 'b'])
for xpt, ypt, pt in decision_boundary:
    ax[0].scatter(xpt, ypt, color=colors[pt[0]], alpha=.15)
    ax[0].scatter(X[:, 0], X[:, 1], color=colors[y], s=30)
ax[0].set_ylim(ymin, ymax)
ax[0].set_xlim(xmin, xmax)
ax[0].set_title("A well separated dataset")

ax[1].scatter(test_points[:, 0], test_points[:, 1],
           color=colors[test_preds], alpha=.25)
ax[1].scatter(X_common[:, 0], X_common[:, 1], color=colors[y])
ax[1].set_title("A well separated dataset")

ax[2].scatter(test_points_rbf[:, 0], test_points_rbf[:, 1],
           color=colors[test_preds_rbf], alpha=.25)
ax[2].scatter(X[:, 0], X[:, 1], color=colors[y])
ax[2].set_title("SVM with a radial basis function")

plt.show()
