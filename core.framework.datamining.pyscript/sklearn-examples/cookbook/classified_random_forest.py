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
# X, y = datasets.make_classification(1000)
X, y = datasets.make_classification(n_samples=10000,
                                    n_features=20,
                                    n_informative=15,
                                    flip_y=.5, weights=[.2,
                                                        .8])

rf = RandomForestClassifier()
rf.fit(X, y)

print "Accuracy:\t", (y == rf.predict(X)).mean()
print "Total Correct:\t", (y == rf.predict(X)).sum()

# 调整随机森林模型
training = np.random.choice([True, False], p=[.8, .2],
                            size=y.shape)

rf.fit(X[training], y[training])
preds = rf.predict(X[~training])
print "Accuracy:\t", (preds == y[~training]).mean()

# 引入模型评估度量之一，准确率是第一个不错的度量，但是使用混淆矩阵会帮助我们理解发生了什么。
# 让我们迭代 max_features 的推荐选项，并观察对拟合有什么影响。
# 我们同时迭代一些浮点值，它们是所使用的特征的分数。使用下列命令：

max_feature_params = ['auto', 'sqrt', 'log2', .01, .5, .99]
confusion_matrixes = {}
for max_feature in max_feature_params:
    rf_confusion = RandomForestClassifier(max_features=max_feature)
    rf_confusion.fit(X[training], y[training])
    # confusion_matrixes[max_feature] = confusion_matrix(y[~training],rf_confusion.predict(X[~training]).ravel())
    confusion_matrixes[max_feature]=( confusion_matrix(y[~training],rf_confusion.predict(X[~training])).ravel())

probs = rf.predict_proba(X)


confusion_df = pd.DataFrame(confusion_matrixes)

#
probs_df = pd.DataFrame(probs, columns=['0', '1'])
probs_df['was_correct'] = rf.predict(X) == y


f, ax = plt.subplots(figsize=(10, 8),nrows=2)

confusion_df.plot(kind='bar', ax=ax[0])
# confusion_df.plot(kind='bar', ax=ax)
ax[0].legend(loc='best')
ax[0].set_title("Guessed vs Correct (i, j) where i is the guessand j is the actual.")
ax[0].grid()

ax[0].set_xticklabels([str((i, j)) for i, j in list (itertools.product(range(2), range(2)))]);
# ax[0].set_xlabel("Guessed vs Correct")
ax[0].set_ylabel("Correct")


probs_df.groupby('0').was_correct.mean().plot(kind='bar', ax=ax[1])
ax[1].set_title("Accuracy at 0 class probability")
ax[1].set_ylabel("% Correct")
# ax[1].set_xlabel("% trees for 0")
plt.show()