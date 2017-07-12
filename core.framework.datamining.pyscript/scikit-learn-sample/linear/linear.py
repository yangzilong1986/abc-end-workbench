#coding=utf-8

from sklearn import linear_model
X= [[0, 0], [1, 1], [2, 2]]
y = [0, 1, 2]
# 普通最小二乘法
# 无偏估计的
# 通过计算最小二乘的损失函数的最小值来求得参数得出模型
# 通常用在观测有误差的情况，解决线性回归问题
#
# 求实际观测值与预测值差的平方最小值

# 缺点：要求每个影响因素相互独立，否则会出现随机误差。
# 回归用于解决预测值问题

clf = linear_model.LinearRegression()
clf.fit(X, y)#拟合模型
print clf.coef_
print clf.intercept_
print clf.predict([[3, 3]])
print clf.decision_function(X)#返回X的预测值y
print clf.score(X, y)#计算
print clf.get_params() #获取LinearRegression构造方法的参数信息
print clf.set_params(fit_intercept = False)


# 有偏估计的，回归系数更符合实际、更可靠，对病态数据的拟合要强于最小二乘
# 数学公式：
# >=0， 越大，w值越趋于一致
# 改良的最小二乘法，增加系数的平方和项和调整参数的积
#
# 是由sklearn.linear_model模块中的Ridge类实现
# Ridge回归用于解决两类问题：一是样本少于变量个数，二是变量间存在共线性
# Ridge的构造方法：
# sklearn.linear_model.Ridge(alpha=1.0               #公式中 的值，默认为1.0
#                            , fit_intercept=True
#                            , normalize=False
#                            , copy_X=True
#                            , max_iter=None     #共轭梯度求解器的最大迭代次数
#                            ,tol=0.001          #默认值0.001
#                            , solver='auto')       #
# Ridge回归复杂性：同最小二乘法

