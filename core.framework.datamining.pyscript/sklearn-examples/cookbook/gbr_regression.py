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

# 梯度提升回归（Gradient boosting regression，GBR）是一种从它的错误中进行学习的技术。
# 它本质上就是集思广益，集成一堆较差的学习算法进行学习。
# 有两点需要注意：
# 每个学习算法准备率都不高，但是它们集成起来可以获得很好的准确率。
# 这些学习算法依次应用，也就是说每个学习算法都是在前一个学习算法的错误中学习
from sklearn.datasets import make_regression
X, y = make_regression(1000, 2, noise=10)

from sklearn.ensemble import GradientBoostingRegressor as GBR
gbr = GBR()
gbr.fit(X, y)
gbr_preds = gbr.predict(X)
print('gbr')
print(gbr)

print('gbr_preds')
print(gbr_preds[:5])
# 很明显，这里应该不止一个模型，但是这种模式现在很简明。现在，让我们用基本回归算法来拟合数据当作参照：

lr = LinearRegression()
lr.fit(X, y)
lr_preds = lr.predict(X)
print('lr')
print(lr)

print('lr_preds')
print(lr_preds[:5])


gbr_residuals = y - gbr_preds
lr_residuals = y - lr_preds

# 看起来好像GBR拟合的更好，
# 但是并不明显。让我们用95%置信区间（Confidenceinterval,CI）对比一下：
# GBR的置信区间更小，数据更集中，因此其拟合效果更好
print '用95%置信区间（Confidenceinterval,CI）对比一下'
print 'gbr_residuals'
print np.percentile(gbr_residuals, [2.5, 97.5])

print 'lr_residuals'
print np.percentile(lr_residuals, [2.5, 97.5])

# 我们还可以对GBR算法进行一些调整来改善效果。
# 我用下面的例子演示一下，然后在下一节介绍优化方法：
n_estimators = np.arange(100, 1100, 350)
gbrs = [GBR(n_estimators=n_estimator) for n_estimator in n_estimators]
residuals = {}
for i, gbr in enumerate(gbrs):
    gbr.fit(X, y)
    residuals[gbr.n_estimators] = y - gbr.predict(X)

# GBR的第一个参数是 n_estimators ，指GBR使用的学习算法的数量。
# 通常，如果你的设备性能更好，可以把 n_estimators 设置的更大，效果也会更好。

# 应该在优化其他参数之前先调整 max_depth 参数。因为每个学习算法都是一颗决策树，
# max_depth 决定了树生成的节点数。选择合适的节点数量可以更好的拟合数据，
# 而更多的节点数可能造成拟合过度。

# loss 参数决定损失函数，也直接影响误差。
# ls 是默认值，表示最小二乘法（least squares）。
# 还有最小绝对值差值，Huber损失和分位数损失（quantiles）



f, ax = plt.subplots(figsize=(7, 5),nrows=2)
f.tight_layout()
ax[0].hist(gbr_residuals,bins=20,label='GBR Residuals', color='b',alpha=.5)
ax[0].hist(lr_residuals,bins=20,label='LR Residuals', color='r', alpha=.5)
ax[0].set_title("GBR Residuals vs LR Residuals")
ax[0].legend(loc='best')

colors = {800:'r', 450:'g', 100:'b'}
for k, v in residuals.items():
    ax[1].hist(v,bins=20,label='n_estimators: %d' % k, color=colors [k], alpha=.5);
ax[1].set_title("Residuals at Various Numbers of Estimators")
ax[1].legend(loc='best');

plt.show()