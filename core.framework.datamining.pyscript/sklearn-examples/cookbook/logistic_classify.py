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
#
from sklearn import linear_model

# 实际上线性模型也可以用于分类任务。
# 方法是把一个线性模型拟合成某个类型的概率分布，然后用一个函数建立阈值来确定结果属于哪一类。
from sklearn.datasets import make_classification
X, y = make_classification(n_samples=1000, n_features=4)

def logistsod():
    f, ax = plt.subplots(figsize=(10, 5))
    rng = np.linspace(-5, 5)
    log_f = np.apply_along_axis(lambda x:1 / (1 + np.exp(-x)), 0, rng)
    ax.set_title("Logistic Function between [-5, 5]")
    ax.plot(rng, log_f);

    plt.show()

# 让我们用 make_classification 方法创建一个数据集来进行分类：
from sklearn.datasets import make_classification
X, y = make_classification(n_samples=1000, n_features=4)
# LogisticRegression 对象和其他线性模型的用法一样：
from sklearn.linear_model import LogisticRegression
lr = LogisticRegression()

# 我们将把前面200个数据作为训练集，最后200个数据作为测试集。
# 因为这是随机数据集，所以用最后200个数据没问题。
# 但是如果处理具有某种结构的数据，就不能这么做了（例如，你的数据集是时间序列数据）：
X_train = X[:-200]
X_test = X[-200:]
y_train = y[:-200]
y_test = y[-200:]

lr.fit(X_train, y_train)
y_train_predictions = lr.predict(X_train)
y_test_predictions = lr.predict(X_test)

# 现在我们有了预测值，让我们看看预测的效果。这里，我们只简单看看预测正确的比例；
# 后面，我们会详细的介绍分类模型效果的评估方法。
# 计算很简单，就是用预测正确的数量除以总样本数：

print (y_train_predictions == y_train).sum().astype(float) / y_train.shape[0]

# 测试集的效果是：
print (y_test_predictions == y_test).sum().astype(float) / y_test.shape[0]

# 可以看到，测试集的正确率和训练集的结果差不多。但是实际中通常差别很大。
# 现在问题变成，怎么把逻辑函数转换成分类方法。

# 下面的内容你以后肯定会遇到。一种情况是一个类型与其他类型的权重不同；
# 例如，一个能可能权重很大，99%。这种情况在分类工作中经常遇到。
# 经典案例就是信用卡虚假交易检测，大多数交易都不是虚假交易，但是不同类型误判的成本相差很大。

# 让我们建立一个分类问题，类型y的不平衡权重95%，我们看看基本的逻辑回归型如何处理这类问题：
X, y = make_classification(n_samples=5000, n_features=4, weights=[.95])
print sum(y) / (len(y)*1.) #检查不平衡的类型

# 建立训练集和测试集，然后用逻辑回归拟合：
X_train = X[:-500]
X_test = X[-500:]
y_train = y[:-500]
y_test = y[-500:]
lr.fit(X_train, y_train)
print('lr-0')
print(lr)
y_train_predictions = lr.predict(X_train)
y_test_predictions = lr.predict(X_test)

# 现在我们在看看模型拟合的情况：
print('现在我们在看看模型拟合的情况:')
print (y_train_predictions == y_train).sum().astype(float) / y_train.shape[0]
print (y_test_predictions == y_test).sum().astype(float) / y_test.shape[0]

# 结果看着还不错，但这是说如果我们把一个交易预测成正常交易（或者称为类型0），
# 那么我们有95%左右的可能猜对。
# 如果我们想看看模型对类型1的预测情况，可能就不是那么好了：
print (y_test[y_test==1] == y_test_predictions[y_test==1]).sum().astype(float) / y_test[y_test==1].shape[0]

print (y_test[y_test==0] == y_test_predictions[y_test==0]).sum().astype(float) / y_test[y_test==0].shape[0]


# 如果相比正常交易，我们更关心虚假交易；那么这是由商业规则决定的，我们可能会改变预测正确和预测错误的权重。
# 通常情况下，虚假交易与正常交易的权重与训练集的类型权重的倒数一致。
# 但是，因为我们更关心虚假交易，所有让我们用多重采样（oversample）方法来表示虚假交易与正常交易的权重。
lr = LogisticRegression(class_weight={0: .15, 1: .85})
lr.fit(X_train, y_train)
print('lr')
print(lr)

y_train_predictions = lr.predict(X_train)
y_test_predictions = lr.predict(X_test)

print('因为我们更关心虚假交易，所有让我们用多重采样（oversample）方法来表示虚假交易与正常交易的权重')
print (y_test[y_test==1] == y_test_predictions[y_test==1]).sum().astype(float) / y_test[y_test==1].shape[0]

print('但是，这么做需要付出什么代价？让我们看看')
print((y_test_predictions == y_test).sum().astype(float) / y_test.shape[0])
