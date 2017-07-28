#encoding:utf-8
from sklearn import datasets
import numpy as np
from sklearn import preprocessing
from sklearn import pipeline
from sklearn import decomposition
from matplotlib import pyplot as plt
from sklearn.datasets import load_boston
from sklearn.gaussian_process import GaussianProcess
#
from sklearn import linear_model

#如果SGD适合处理大数据集，我们就用大点儿的数据集来演示
X, y = datasets.make_regression(int(1e6))
print("{:,}".format(int(1e6)))#<type 'tuple'>: (1000000L, 100L)
# 值得进一步了解数据对象的构成和规模信息。还在我们用的是NumPy数组，所以我
# 们可以获得 nbytes 。Python本身没有获取NumPy数组大小的方法。输出结果与系统有关，你的结果和下面的数据可能不同：
print("{:,}".format(X.nbytes))
# 我们把字节码 nbytes 转换成MB（megabytes），看着更直观：
mb=X.nbytes / 1e6
print (mb)
# 因此，每个数据点的字节数就是：
nu=X.nbytes / (X.shape[0] * X.shape[1])
print (nu)

#数据，就用 SGDRegressor 来拟合
sgd = linear_model.SGDRegressor()
train = np.random.choice([True, False], size=len(y), p=[.75, .25])
model_result=sgd.fit(X[train], y[train])
print(model_result)

# 这里又出现一个“充实的（beefy）”对象。重点需要了解我们的损失函数是 squared_loss ，
# 与线性回归里的残差平方和是一样的。还需要注意 shuffle 会对数据产生随机搅动（shuffle），这在解决伪相关问题时很有用。
linear_preds = sgd.predict(X[~train])
print(u'预测')
print(linear_preds)

# 当每天一个市场都有20亿条交易数据。现在如果有一周或一年的数据，
# 用SGD算法就可能无法运行了。很难处理这么大的数据量，
# 因为标准的梯度下降法每一步都要计算梯度，计算量非常庞大。
# 标准的梯度下降法的思想是在每次迭代计算一个新的相关系数矩阵，
# 然后用学习速率（learning rate）和目标函数（objective function）的梯度调整它，
# 直到相关系数矩阵收敛为止。
# 每次更新之前，我们都需要对每个数据点计算新权重。

# SGD的工作方式稍有不同；每次迭代不是批量更新梯度，而是只更新新数据点的参数。
# 这些数据点是随机选择的，因此称为随机梯度下降法。

plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['font.family']='sans-serif'
f, ax = plt.subplots(figsize=(7, 5))
f.tight_layout()
ax.hist(linear_preds - y[~train],label='Residuals Linear', color=
'b', alpha=.5);
ax.set_title("Residuals")
ax.legend(loc='best');
plt.show()