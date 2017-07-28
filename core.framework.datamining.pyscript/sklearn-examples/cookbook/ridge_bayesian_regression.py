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
from sklearn.datasets import make_regression
from scipy.stats import gamma
from sklearn.linear_model import BayesianRidge


X, y = make_regression(1000, 10, n_informative=2, noise=20)

br = BayesianRidge()
# 有两组相关系数，分别是 alpha_1 / alpha_2 和 lambda_1 / lambda_2
br.fit(X, y)
print('相关系数')
print br.coef_

br_alphas = BayesianRidge(alpha_1=10, lambda_1=10)
br_alphas.fit(X, y)

print('调整之后相关系数')
print br_alphas.coef_

# 因为是贝叶斯岭回归，我们假设先验概率分布带有误差和alpha参数.
# 先验概率分布都服从Gamma分布。
# Gamma分布是一种极具灵活性的分布。不同的形状参数和尺度参数的Gamm分布形状有差异。
# 1e-06是 scikit-learn里面 BayesianRidge 形状参数的默认参数值。
form = lambda x, y: "loc={}, scale={}".format(x, y)
g = lambda x, y=1e-06, z=1e-06: gamma.pdf(x, y, z)
g2 = lambda x, y=1e-06, z=1: gamma.pdf(x, y, z)
g3 = lambda x, y=1e-06, z=2: gamma.pdf(x, y, z)

# 一种套索回归的贝叶斯解释。我们把先验概率分布看出是相关系数的函数；
# 它们本身都是随机数。对于套索回归，我们选择一个可以产生0的分布，
# 比如双指数分布（Double Exponential Distribution，也叫Laplace
# distribution）。
from scipy.stats import laplace
form = lambda x, y: "loc={}, scale={}".format(x, y)
g4 = lambda x: laplace.pdf(x)
rng4 = np.linspace(-5, 5)

rng = np.linspace(-5, 5)
f, ax = plt.subplots(figsize=(8, 5),nrows=2)

ax[0].plot(rng, list(map(g, rng)), label=form(1e-06, 1e-06), color='r')
ax[0].plot(rng, list(map(g2, rng)), label=form(1e-06, 1), color='g')
ax[0].plot(rng, list(map(g3, rng)), label=form(1e-06, 2), color='b' )
ax[0].set_title("Different Shapes of the Gamma Distribution")
ax[0].legend()


ax[1].plot(rng, list(map(g4, rng4)), color='r')
ax[1].set_title("Example of Double Exponential Distribution");
ax[1].legend()
plt.show()