# -*- coding: utf-8 -*-
"""
========================
Plotting Learning Curves
========================

On the left side the learning curve of a naive Bayes classifier is shown for
the digits dataset.
朴素贝叶斯分类的学习曲线
Note that the training score and the cross-validation score are both not very good at the end.

However, the shape of the curve can be found in more complex datasets very often:
the training score is very high at the beginning and decreases and the cross-validation score is very low at the
beginning and increases.
训练成绩在开始时是非常高的，并且在交叉测试中得分很低。开始和增加。

On the right side we see the learning curve of an SVM
with RBF kernel. We can see clearly that the training score is still around
the maximum and the validation score could be increased with more training
samples.
我们可以清楚地看到，训练成绩仍然存在。可以通过增加训练来增加最大值和验证分数。

训练集的准确率很高，验证集的也随着数据量增加而增加，不过因为训练集的还是高于验证集的，
有点过拟合，所以还是需要增加数据量，这时增加数据会对效果有帮助
"""
print(__doc__)

import numpy as np
import matplotlib.pyplot as plt
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.datasets import load_digits
from sklearn.model_selection import learning_curve
from sklearn.model_selection import ShuffleSplit


def plot_learning_curve(estimator, title, X, y, ylim=None, cv=None,
                        n_jobs=1, train_sizes=np.linspace(.1, 1.0, 5),
                        pltnumber=1):
    plt.subplot(1, 2, pltnumber)
    plt.title(title)
    if ylim is not None:
        plt.ylim(*ylim)
    plt.xlabel("Training examples")
    plt.ylabel("Score")
    train_sizes, train_scores, test_scores = learning_curve(
        estimator, X, y, cv=cv, n_jobs=n_jobs, train_sizes=train_sizes)
    #训练样本
    # 训练 按照列求均值
    train_scores_mean = np.mean(train_scores, axis=1)
    #求标准抓
    train_scores_std = np.std(train_scores, axis=1)
    #测试样本
    test_scores_mean = np.mean(test_scores, axis=1)
    test_scores_std = np.std(test_scores, axis=1)
    plt.grid()
    # fill_between 填充两个函数之间的区域
    # 两个函数之间的区域用黄色填充
    plt.fill_between(train_sizes, train_scores_mean - train_scores_std,
                     train_scores_mean + train_scores_std, alpha=0.1,
                     color="r")
    plt.fill_between(train_sizes, test_scores_mean - test_scores_std,
                     test_scores_mean + test_scores_std, alpha=0.1, color="g")
    plt.plot(train_sizes, train_scores_mean, 'o-', color="r",
             label="Training score")
    plt.plot(train_sizes, test_scores_mean, 'o-', color="g",
             label="Cross-validation score")

    plt.legend(loc="best")
    return plt


digits = load_digits()
X, y = digits.data, digits.target

plt.figure(figsize=(14, 5))
title = "Learning Curves (Naive Bayes)"
# Cross validation with 100 iterations to get smoother mean test and train
# score curves, each time with 20% data randomly selected as a validation set.
cv = ShuffleSplit(n_splits=100, test_size=0.2, random_state=0)

estimator = GaussianNB()
plot_learning_curve(estimator, title, X, y, ylim=(0.7, 1.01), cv=cv,pltnumber=1)

# plot_learning_curve(estimator, title, X, y, ylim=(0.7, 1.01), cv=cv, n_jobs=4)
title = "Learning Curves (SVM, RBF kernel, $\gamma=0.001$)"
# SVC is more expensive so we do a lower number of CV iterations:
cv = ShuffleSplit(n_splits=10, test_size=0.2, random_state=0)
estimator = SVC(gamma=0.001,degree=2)
plot_learning_curve(estimator, title, X, y, (0.7, 1.01) ,pltnumber=2)
# plot_learning_curve(estimator, title, X, y, (0.7, 1.01), cv=cv, n_jobs=4)
plt.show()
