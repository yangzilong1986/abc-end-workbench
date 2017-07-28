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
# boston 数据集很适合用来演示线性回归。 boston 数据集包含了波士顿地区的
# 房屋价格中位数。还有一些可能会影响房价的因素，比如犯罪率（crime rate）。
boston = datasets.load_boston()

# lr 对象已经拟合过数据
# 每当拟合工作做完之后，我们应该问的第一个问题就是“拟合的效果如何？”本主题将回答这个问题。
lr = LinearRegression()
lr.fit(boston.data, boston.target)
predictions = lr.predict(boston.data)

# 误差项服从均值为0的正态分布。残差就是误差，所以这个图也应该近似正态分布。
# 看起来拟合挺好的，只是有点偏。我们计算一下残差的均值，应该很接近0：
# 我们可以看到一些简单的量度（metris）和图形。让我们看看上一章的残差图：
mse=np.mean(boston.target - predictions)
print('残差')
print(mse)

# 均方误差（mean squared error，MSE）
def MSE(target, predictions):
    squared_deviation = np.power(target - predictions, 2)
    return np.mean(squared_deviation)

# 平均绝对误差（mean absolute deviation，MAD）
def MAD(target, predictions):
    absolute_deviation = np.abs(target - predictions)
    return np.mean(absolute_deviation)

# 计算预测值与实际值的差，平方之后再求平均值。这其实就是我们寻找最佳相关系数时是目标。
# 高斯－马尔可夫定理（Gauss-Markov theorem）实际上已经证明了线性回归的回归系数的最佳线性无偏估计（BLUE）
# 就是最小均方误差的无偏估计（条件是误差变量不相关，0均值，同方差）
mse=MSE(boston.target, predictions)
mad=MAD(boston.target, predictions)
print('均方误差')
print(mse)

print('平均绝对误差')
print(mad)

# 相关系数是随机变量，因此它们是有分布的。让我们用bootstrapping（重复试验）
# 来看看犯罪率的相关系数的分布情况。
# bootstrapping是一种学习参数估计不确定性的常用手段：
n_bootstraps = 1000
len_boston = len(boston.target)
subsample_size = np.int(0.5*len_boston)
subsample = lambda: np.random.choice(np.arange(0, len_boston),size=subsample_size)
coefs = np.ones(n_bootstraps) #相关系数初始值设为1
for i in range(n_bootstraps):
    subsample_idx = subsample()
    subsample_X = boston.data[subsample_idx]
    subsample_y = boston.target[subsample_idx]
    lr.fit(subsample_X, subsample_y)
    coefs[i] = lr.coef_[0]

# 重复试验后的置信区间：
#置信区间的范围表明犯罪率其实不影响房价，
# 因为0在置信区间里面，表面犯罪率可能与房价无关。
Confidence_interval=np.percentile(coefs, [2.5, 97.5])
print('重复试验后的置信区间')
print(Confidence_interval)

plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['font.family']='sans-serif'
# subplot函数中有三个整数参数，前两个指定制图的行、列.
f, ax = plt.subplots(figsize=(7, 5), nrows=3)

# f, ax = plt.subplots(figsize=(7, 5))
f.tight_layout()
ax[0].hist(boston.target - predictions,bins=40, label='Residuals Linear', color='b', alpha=.5)
ax[0].set_title(u"残差直方图Histogram of Residuals")
ax[0].legend(loc='best')
# 另一个值得看的图是Q-Q图（分位数概率分布），我们用Scipy来实现图形，因为它
# 内置这个概率分布图的方法：
probplot(boston.target - predictions, plot=ax[1])


ax[2].hist(coefs, bins=50, color='b', alpha=.5)
ax[2].set_title(u"直方图Histogram of the lr.coef_[0].");

f.savefig('D:/DevN/data-mining/spark-sample-data/boston_Histogram_Residualsmyfig.png')
plt.show()






