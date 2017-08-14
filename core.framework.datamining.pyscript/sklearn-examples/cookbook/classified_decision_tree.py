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

n_features=200
# X, y = datasets.make_classification(n_samples=1000, n_features=3, n_redundant=0)
X, y = datasets.make_classification(750, n_features, n_informative=5)
from sklearn.tree import DecisionTreeClassifier
dt = DecisionTreeClassifier()
dicsi_model=dt.fit(X, y)

print('dicsi_model')
print(dicsi_model)

preds = dt.predict(X)

print  (y == preds).mean()

training = np.random.choice([True, False], p=[.75, .25], size=len(y))

X, y = datasets.make_classification(1000, 20, n_informative=3)
dt = DecisionTreeClassifier()
dt.fit(X, y)
from StringIO import StringIO
from sklearn import tree
import pydot
str_buffer = StringIO()
tree.export_graphviz(dt, out_file=str_buffer)
graph = pydot.graph_from_dot_data(str_buffer.getvalue())
graph.write("D:/DevN/sample-data/spark-data/myfile.jpg")

accuracies = []
for x in np.arange(1, n_features+1):
    dt = DecisionTreeClassifier(max_depth=x)
    dt.fit(X[training], y[training])
    preds = dt.predict(X[~training])
    accuracies.append((preds == y[~training]).mean())
    # accuracies.append([x,(preds == y[~training]).mean()])

# 实际上在较低最大深度处得到了漂亮的准确率。让我们进一步看看低级别的准确率，首先是 15：
# accuraciesmat=np.mat(accuracies)
# max=np.argsort(accuraciesmat , axis=0)
accuraciesnp=np.array(accuracies)
accuraciesSort = np.argsort(-accuraciesnp)# sorted(accuracies,reverse=True)
N = accuraciesSort[0]
print('最大值位置')
print(N)
#最大值为
print('top5')
print(accuraciesSort[:5])



f, ax = plt.subplots(figsize=(7, 5),nrows=2)
# ax[0].plot(range(1, n_features+1), accuraciesmat[:,1], color='k')
ax[0].plot(range(1, n_features+1), accuracies, color='k')
ax[0].set_title("Decision Tree Accuracy")
ax[0].set_ylabel("% Correct")
ax[0].set_xlabel("Max Depth")


# ax[1].plot(range(1, n_features+1)[:N], accuraciesmat[:,1][N], color='k' )
ax[1].plot(range(1, n_features+1)[:N], accuracies[:N], color='k')
ax[1].set_title("Decision Tree Accuracy")
ax[1].set_ylabel("% Correct")
ax[1].set_xlabel("Max Depth")
plt.show()

