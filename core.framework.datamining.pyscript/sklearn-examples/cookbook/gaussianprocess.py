#encoding:utf-8
from sklearn import datasets
import numpy as np
from sklearn import preprocessing
from sklearn import pipeline
from sklearn import decomposition
from matplotlib import pyplot as plt
from sklearn.datasets import load_boston
from sklearn.gaussian_process import GaussianProcess

boston = load_boston()
boston_X = boston.data
boston_y = boston.target
train_set = np.random.choice([True, False], len(boston_y), p=[.75 , .25])

gp = GaussianProcess()
result=gp.fit(boston_X[train_set], boston_y[train_set])
test_preds,test_sample_mse = gp.predict(boston_X[~train_set],eval_MSE=True)

#线性
gplinear = GaussianProcess(regr='linear', theta0=5e-1)
gplinear.fit(boston_X[train_set], boston_y[train_set])
#
linear_preds,linear_sample_mse = gplinear.predict(boston_X[~train_set],eval_MSE=True)

#MSE
ormes=np.power(test_preds - boston_y[~train_set], 2).mean()

linear_mes=np.power(linear_preds - boston_y[~train_set], 2).mean()

print ("估计-mse")
print(test_sample_mse[:5])
print(linear_sample_mse[:5])
print ("mse")
print(ormes)
print(linear_mes)

plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['font.family']='sans-serif'

plt.rcParams['axes.unicode_minus'] = False
f, ax = plt.subplots(figsize=(10, 7), nrows=5)
f.tight_layout()
#我们把预测值和实际值画出来比较
ax[0].plot(range(len(test_preds)), test_preds, color='b',label=u'预测值PredictedValues');
ax[0].plot(range(len(test_preds)), linear_preds, color='r', label=u'线性预测值li-PredictedValues');
ax[0].plot(range(len(test_preds)), boston_y[~train_set], color='k', label=u'实际值ctual Values');
# ax[0].set_title("实际值与预测值Predicted vs Actuals")
ax[0].legend(loc='best')

ax[0].set_title(u'预测值与实际值')


ax[1].plot(range(len(test_preds)),
           test_preds - boston_y[~train_set]);
ax[1].set_title(u'残差散点图Plotted Residuals')

ax[2].hist(test_preds - boston_y[~train_set]);
ax[2].set_title(u'残差直方图Histogram of Residuals');



ax[3].hist(test_preds - boston_y[~train_set],label='Residuals Original', color='b', alpha=.5);
ax[3].hist(linear_preds - boston_y[~train_set],label='Residuals Linear', color='r', alpha=.5);
ax[3].set_title("Residuals")
ax[3].legend(loc='best')

n = 120
rng = range(n)
ax[4].scatter(rng, linear_preds[:n])
ax[4].errorbar(rng, linear_preds[:n], yerr=1.96*linear_sample_mse[:n])
ax[4].set_title("Predictions with Error Bars")
ax[4].set_xlim((-20, 140));
ax[4].hist(linear_preds - boston_y[~train_set]);
ax[4].set_title(u'残差直方图Histogram of Residuals');

plt.show()