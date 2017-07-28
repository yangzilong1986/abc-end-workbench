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
from sklearn.datasets import make_regression

# 首先我们用 make_regression 建一个有3个自变量的数据集，但是其秩为2，因此3个自变量中有两个自变量存在相关性。
reg_data, reg_target = make_regression(n_samples=2000, n_features=3, effective_rank=2, noise=10)

from sklearn import linear_model
# boston 数据集很适合用来演示线性回归。 boston 数据集包含了波士顿地区的
# 房屋价格中位数。还有一些可能会影响房价的因素，比如犯罪率（crime rate）。
boston = datasets.load_boston()

# lr 对象已经拟合过数据
# 每当拟合工作做完之后，我们应该问的第一个问题就是“拟合的效果如何？”本主题将回答这个问题。
lr = LinearRegression()
lr.fit(boston.data, boston.target)
predictions = lr.predict(boston.data)#<type 'tuple'>: (506L,)

# print('线性规划')
# print lr
# print ('预测结果')
# print predictions[:5]
#
# n_bootstraps = 1000
# len_boston = len(boston.target)
# subsample_size = np.int(0.5*len_boston)
# subsample = lambda: np.random.choice(np.arange(0, len_boston),size=subsample_size)
# coefs = np.ones(n_bootstraps) #相关系数初始值设为1
# for i in range(n_bootstraps):
#     subsample_idx = subsample()
#     subsample_X = boston.data[subsample_idx]
#     subsample_y = boston.target[subsample_idx]
#     lr.fit(subsample_X, subsample_y)
#     coefs[i] = lr.coef_[0]
#
# coefs_linear=np.var(coefs, axis=0)
# print('相关系数的方差-线性回归')
# print(coefs_linear)
#
# f = plt.figure(figsize=(7, 5))
# ax = f.add_subplot(111)
# ax.hist(coefs, bins=50, color='b', alpha=.5)
# ax.set_title("Histogram of the lr.coef_[0].")
# plt.show()

def fit_2_regression(lr):
    n_bootstraps = 1000
    coefs = np.ones((n_bootstraps, 3))
    len_data = len(reg_data)
    subsample_size = np.int(0.75*len_data)
    subsample = lambda: np.random.choice(np.arange(0, len_data),
                                         size=subsample_size)
    for i in range(n_bootstraps):
        subsample_idx = subsample()
        subsample_X = reg_data[subsample_idx]
        subsample_y = reg_target[subsample_idx]
        lr.fit(subsample_X, subsample_y)
        coefs[i][0] = lr.coef_[0]
        coefs[i][1] = lr.coef_[1]
        coefs[i][2] = lr.coef_[2]

    # import matplotlib.pyplot as plt
    # f, axes = plt.subplots(nrows=3, sharey=True, sharex=True, figsize=(7, 5))
    # f.tight_layout()
    # for i, ax in enumerate(axes):
    #     ax.hist(coefs[:, i], color='b', alpha=.5)
    #     ax.set_title("Coef {}".format(i))
    #
    # plt.show()
    return coefs
coefs = fit_2_regression(lr)
print('相关系数的方差-线性回归')
# print(coefs)
print(np.var(coefs, axis=0))
