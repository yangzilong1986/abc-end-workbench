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
X, y = datasets.make_classification(n_samples=10000,
                                    n_features=20,
                                    n_informative=15,
                                    flip_y=.5, weights=[.2,
                                                        .8])

# 调整随机森林模型
training = np.random.choice([True, False], p=[.8, .2],
                            size=y.shape)
n_estimator_params = range(1, 20)
confusion_matrixes = {}
for n_estimator in n_estimator_params:
    rf = RandomForestClassifier(n_estimators=n_estimator)
    rf.fit(X[training], y[training])
    confusion_matrixes[n_estimator] = confusion_matrix(y[~training],rf.predict(X[~training]))
    accuracy = lambda x: np.trace(x) / np.sum(x, dtype=float)
    confusion_matrixes[n_estimator] = accuracy(confusion_matrixes[n_estimator])

accuracy_series = pd.Series(confusion_matrixes)

f, ax = plt.subplots(figsize=(7, 5))
accuracy_series.plot(kind='bar', ax=ax, color='k', alpha=.75)
ax.grid()
ax.set_title("Accuracy by Number of Estimators")
ax.set_ylim(0, 1) # we want the full scope
ax.set_ylabel("Accuracy")
ax.set_xlabel("Number of Estimators")
plt.show()