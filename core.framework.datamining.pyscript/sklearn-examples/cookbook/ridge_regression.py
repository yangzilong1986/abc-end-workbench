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

#它引入了正则化参数来“缩减”相关系数。当数据集中存在共线因素时，岭回归会很有用。
from sklearn import linear_model
# boston 数据集很适合用来演示线性回归。 boston 数据集包含了波士顿地区的
# 房屋价格中位数。还有一些可能会影响房价的因素，比如犯罪率（crime rate）。
# boston = datasets.load_boston()

from sklearn.datasets import make_regression

# 首先我们用 make_regression 建一个有3个自变量的数据集，但是其秩为2，因此3个自变量中有两个自变量存在相关性。
reg_data, reg_target = make_regression(n_samples=2000, n_features=3, effective_rank=2, noise=10)

lr = LinearRegression()

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
    # plt.show()
    return coefs

coefs = fit_2_regression(lr)

from sklearn.linear_model import Ridge
coefs_r = fit_2_regression(Ridge())

# 从均值上看，线性回归比岭回归的相关系数要更很多。
ring_m=np.mean(coefs - coefs_r, axis=0)
print('均值-岭回归')
print(ring_m)

# 均值显示的差异其实是线性回归的相关系数隐含的偏差。
# 岭回归的相关系数方差也会小很多。
# 这就是机器学习里著名的偏差-方差均衡(Bias-Variance Trade-off)。
# 相关系数的方差：
coefs_linear=np.var(coefs, axis=0)
coefs_ridge=np.var(coefs_r, axis=0)
print('相关系数的方差-线性回归')
print(coefs_linear)

print('相关系数的方差-岭回归')
print(coefs_ridge)

# 当你使用岭回归模型进行建模时，需要考虑 Ridge 的 alpha 参数。
# 例如，用OLS（普通最小二乘法）做回归也许可以显示两个变量之间的某些关系；
# 但是，当 alpha 参数正则化之后，那些关系就会消失。做决策时，这些关系是否
# 需要考虑就显得很重要了。
# 2.模型参数优化
# 岭回归RidgeRegression 的 alpha 参数；
# 因此，问题就是最优的 alpha 参数是什么。首先我们建立回归数据集：
# reg_data, reg_target = make_regression(n_samples=2000, n_features=3, effective_rank=2, noise=10)
# reg_data, reg_target = make_regression(n_samples=100, n_features=2, effective_rank=1, noise=10)
# 在 linear_models 模块中，有一个对象叫 RidgeCV ，表示岭回归交叉检验（ridge cross-validation）。
# 这个交叉检验类似于留一交叉验证法（leave-oneoutcross-validation，LOOCV）。这种方法是指训练数据时留一个样本，
# 测试的时候用这个未被训练过的样本：
from sklearn.linear_model import RidgeCV
rcv = RidgeCV(alphas=np.array([.1, .2, .3, .4]))
rcv.fit(reg_data, reg_target)
# 拟合模型之后， alpha 参数就是最优参数：
print(rcv.alpha_)

# 0.1 是最优参数，我们还想看到 0.1 附近更精确的值：
rcv = RidgeCV(alphas=np.array([.08, .09, .1, .11, .12]))
rcv.fit(reg_data, reg_target)

print(rcv.alpha_)

#测试了0.0001到0.05区间中的50个点。由于我们把 store_cv_values 设置成 true ，
# 我们可以看到每一个值对应的拟合效果
alphas_to_test = np.linspace(0.0001, 0.05)
rcv3 = RidgeCV(alphas=alphas_to_test, store_cv_values=True)
rcv3.fit(reg_data, reg_target)

print(rcv3.cv_values_.shape)
# 通过100个样本的回归数据集，我们获得了50个不同的 alpha 值。
# 我们可以看到50个误差值，最小的均值误差对应最优的 alpha 值：
smallest_idx = rcv3.cv_values_.mean(axis=0).argmin()
print('最小的均值误差对应最优的alpha 值')
print(alphas_to_test[smallest_idx])
print(rcv3.alpha_)

# f, ax = plt.subplots(figsize=(7, 5))
# ax.set_title(r"Various values of $\alpha$")
# xy = (alphas_to_test[smallest_idx],
#       rcv3.cv_values_.mean(axis=0)[smallest_idx])
# xytext = (xy[0] + .01, xy[1] + .1)
# ax.annotate(r'Chosen $\alpha$', xy=xy, xytext=xytext,
#             arrowprops=dict(facecolor='black', shrink=0, width=0
#                             )
#             )
# ax.plot(alphas_to_test, rcv3.cv_values_.mean(axis=0));
#
# plt.show()

# LASSO（ least absolute shrinkage and selection operator，最小绝对值收缩和选择算子）方法
# 与岭回归和LARS（least angle regression，最小角回归）很类似。与岭回归类似，
# 它也是通过增加惩罚函数来判断、消除特征间的共线性。与LARS相
# 似的是它也可以用作参数选择，通常得出一个相关系数的稀疏向量。

# 2.岭回归也不是万能药。有时就需要用LASSO回归来建模。
# 下面将用不同的损失函数，因此就要用对应的效果评估方法。
reg_data, reg_target = make_regression(n_samples=200, n_features=500, n_informative=5, noise=5)
# lasso 用来调整 lasso 的惩罚项，现在我们用默认值 1 。
# 另外，和岭回归类似，如果设置为 0 ，那么 lasso 就是线性回归：
from sklearn.linear_model import Lasso
lasso = Lasso()
lasso_model=lasso.fit(reg_data, reg_target)
print(lasso_model)

# 再让我们看看还有多少相关系数非零：
import numpy as np
non_zero_alpha=np.sum(lasso.coef_ != 0)
print('lasso相关系数')
print(non_zero_alpha)

lasso_0 = Lasso(0)
lasso_0.fit(reg_data, reg_target)
line_alpha=np.sum(lasso_0.coef_ != 0)
print('line相关系数')
print(line_alpha)

# 对线性回归来说，我们是最小化残差平方和。而LASSO回归里，
# 我们还是最小化残差平方和，但是加了一个惩罚项会导致稀疏。

# LASSO回归的约束创建了围绕原点的超立方体（相关系数是轴），
# 也就意味着大多数点都在各个顶点上，那里相关系数为0。
# 而岭回归创建的是超平面，因为其约束是L2范数，少一个约束，
# 但是即使有限制相关系数也不会变成0。

# LASSO交叉检验
from sklearn.linear_model import LassoCV
lassocv = LassoCV()
lassocv_cv=lassocv.fit(reg_data, reg_target)
print(' LASSO交叉检验')
print(lassocv_cv)
print(lassocv.alpha_)
print lassocv.coef_[:5]
print np.sum(lassocv.coef_ != 0)

# LASSO特征选择
# LASSO通常用来为其他方法所特征选择。
# 例如，你可能会用LASSO回归获取适当的特征变量，然后在其他算法中使用。

# 要获取想要的特征，需要创建一个非零相关系数的列向量，然后再其他算法拟合：
mask = lassocv.coef_ != 0
new_reg_data = reg_data[:, mask]
print new_reg_data.shape

# LARS正则化
# LARS是一种回归手段，适用于解决高维问题
# 首先让我们导入必要的对象。这里我们用的数据集是200个数据，500个特征。
# 我们还设置了一个低噪声，和少量提供信息的（informative）特征：
reg_data, reg_target = make_regression(n_samples=200,n_features=500, n_informative=10, noise=2)

# 由于我们用了10个信息特征，因此我们还要为LARS设置10个非0的相关系数。
# 我们事先可能不知道信息特征的准确数量，但是出于试验的目的是可行的：
from sklearn.linear_model import Lars
lars = Lars(n_nonzero_coefs=10)
lars_model=lars.fit(reg_data, reg_target)
print lars_model

# 检验一下看看LARS的非0相关系数的和：
print np.sum(lars.coef_ != 0)

# 问题在于为什么少量的特征反而变得更加有效。要证明这一点，让我们用一半数量
# 来训练两个LARS模型，一个用12个非零相关系数，另一个非零相关系数用默认值。
# 这里用12个是因为我们对重要特征的数量有个估计，但是可能无法确定准确的数量：
train_n = 100
lars_12 = Lars(n_nonzero_coefs=12)
lars_12_model=lars_12.fit(reg_data[:train_n], reg_target[:train_n])
print np.sum(lars_12.coef_ != 0)
print lars_12
lars_500 = Lars() #默认就是500
lars_500.fit(reg_data[:train_n], reg_target[:train_n])
print np.sum(lars_500.coef_ != 0)
print lars_500

# 现在，让我们看看拟合数据的效果如何，如下所示：
print np.mean(np.power(reg_target[train_n:] - lars.predict(reg_data[train_n:]), 2))

print np.mean(np.power(reg_target[train_n:] - lars_12.predict(reg_data[train_n:]), 2))

print np.mean(np.power(reg_target[train_n:] - lars_500.predict(reg_data[train_n:]), 2))

# LARS通过重复选择与残存变化相关的特征。从图上看，相关性实际上就是特征与残差之间的最小角度；这就是LARS名称的由来。
from sklearn.linear_model import LarsCV
lcv = LarsCV()
lcv.fit(reg_data, reg_target)
print np.sum(lcv.coef_ != 0)

import matplotlib.pyplot as plt
def unit(*args):
    squared = map(lambda x: x**2, args)
    distance = sum(squared) ** (.5)
    return map(lambda x: x / distance, args)

f, ax = plt.subplots(nrows=3, figsize=(5, 10))
plt.tight_layout()
ax[0].set_ylim(0, 1.1)
ax[0].set_xlim(0, 1.1)
x, y = unit(1, 0.02)
ax[0].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[0].text(x + .05, y + .05, r"$x_1$")
x, y = unit(.5, 1)
ax[0].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[0].text(x + .05, y + .05, r"$x_2$")
x, y = unit(1, .45)
ax[0].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[0].text(x + .05, y + .05, r"$y$")
ax[0].set_title("No steps")
# step 1
ax[1].set_title("Step 1")
ax[1].set_ylim(0, 1.1)
ax[1].set_xlim(0, 1.1)
x, y = unit(1, 0.02)
ax[1].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[1].text(x + .05, y + .05, r"$x_1$")
x, y = unit(.5, 1)
ax[1].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[1].text(x + .05, y + .05, r"$x_2$")
x, y = unit(.5, 1)
ax[1].arrow(.5, 0.01, x, y, ls='dashed', edgecolor='black', facecolor='black')
ax[1].text(x + .5 + .05, y + .01 + .05, r"$x_2$")
ax[1].arrow(0, 0, .47, .01, width=.0015, edgecolor='black', facecolor='black')
ax[1].text(.47-.15, .01 + .03, "Step 1")
x, y = unit(1, .45)
ax[1].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[1].text(x + .05, y + .05, r"$y$")
# step 2
ax[2].set_title("Step 2")
ax[2].set_ylim(0, 1.1)
ax[2].set_xlim(0, 1.1)
x, y = unit(1, 0.02)
ax[2].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[2].text(x + .05, y + .05, r"$x_1$")

x, y = unit(.5, 1)
ax[2].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[2].text(x + .05, y + .05, r"$x_2$")
x, y = unit(.5, 1)
ax[2].arrow(.5, 0.01, x, y, ls='dashed', edgecolor='black', facecolor='black')
ax[2].text(x + .5 + .05, y + .01 + .05, r"$x_2$")
ax[2].arrow(0, 0, .47, .01, width=.0015, edgecolor='black', facecolor='black')
ax[2].text(.47-.15, .01 + .03, "Step 1")

## step 2
x, y = unit(1, .45)
ax[2].arrow(.5, .02, .4, .35, width=.0015, edgecolor='black', facecolor='black')
ax[2].text(x, y - .1, "Step 2")
x, y = unit(1, .45)
ax[2].arrow(0, 0, x, y, edgecolor='black', facecolor='black')
ax[2].text(x + .05, y + .05, r"$y$");
plt.show()

