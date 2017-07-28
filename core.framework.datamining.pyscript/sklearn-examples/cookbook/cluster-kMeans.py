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
# 聚类是个非常实用的技巧。通常，我们在采取行动时需要分治。
# 考虑公司的潜在客户列表。公司可能需要将客户按类型分组，之后为这些分组划分职责。
# 聚类可以使这个过程变得容易。
from sklearn.datasets import make_blobs

from sklearn.cluster import KMeans
#构造数据
blobs, classes = make_blobs(500, centers=3)

# 可以使用 KMeans 来寻找这些簇的形心。第一个例子中，我们假装知道有三个形心。
kmean = KMeans(n_clusters=3)

km=kmean.fit(blobs)
print('kmean-model')
print(km)

print('kmean.cluster_centers_')
print(kmean.cluster_centers_)

print('kmean.labels_[:5]')
print(kmean.labels_[:5])

print('classes[:5]')
print(classes[:5])

print('transform 函数十分有用，它会输出每个点到形心的距离')
print(kmean.transform(blobs)[:5])

# 首先，我们查看轮廓（Silhouette）距离。轮廓距离是簇内不相似性、最近的簇间
# 不相似性、以及这两个值最大值的比值。它可以看做簇间分离程度的度量。
# 让我们看一看数据点到形心的距离分布，理解轮廓距离非常有用。
from sklearn import metrics
silhouette_samples = metrics.silhouette_samples(blobs,
                                                kmean.labels_)

sil_model=np.column_stack((classes[:5], silhouette_samples[:5]))
print('silhouette_samples[:5]')
print(silhouette_samples[:5])

print('数据点到形心的距离分布，理解轮廓距离')
print(sil_model)

#轮廓系数的均值通常用于描述整个模型的拟合度
print(silhouette_samples.mean())

#让我们训练多个簇的模型，并看看平均得分是什么样：
blobsmost, classesmost = make_blobs(500, centers=10)
sillhouette_avgs = []
for k in range(2, 60):
    kmeannost = KMeans(n_clusters=k).fit(blobsmost)
    sillhouette_avgs.append(metrics.silhouette_score(blobsmost,
                                                     kmeannost.labels_))

# 轮廓均值随着形心数量的变化情况。我们可以看到最优的数量是3，根据所生成的数据。
# 但是最优的数量看起来是 6 或者 7。这就是聚类的实际情况，十分普遍，
# 我们不能获得正确的簇数量，我们只能估计簇数量的近似值。

#1.能够将聚类表现看做分类练习，在其语境中有用的方法在这里也有用：
print('聚类表现看做分类')
for i in range(3):
    print (kmean.labels_ == classes)[classes == i].astype(int).mean()


# 很显然我们有一些错乱的簇。所以让我们将其捋直，之后我们查看准确度。
ground_truth=classes.copy()
new_ground_truth = classes.copy()
new_ground_truth[ground_truth == 0] = 2
new_ground_truth[ground_truth == 2] = 0
print('整理之后')
for i in range(3):
    print (kmean.labels_ == new_ground_truth)[ground_truth == i].astype(int).mean()

from sklearn import metrics

# 相似性度量是互信息（ mutualinformation score）得分。
print('相似性度量是互信息（ mutualinformation score）得分。')
print metrics.normalized_mutual_info_score(ground_truth, kmean.labels_)

print metrics.normalized_mutual_info_score(ground_truth, ground_truth)

print '通过名称，我们可以分辨出可能存在未规范化的 mutual_info_score'
print(metrics.mutual_info_score(ground_truth, kmean.labels_))

# 惯性是每个数据点和它所分配的簇的平方差之和。我们可以稍微使用 NumPy 来计算它：
kmean.inertia_

#2. MiniBatch KMeans
from sklearn.datasets import make_blobs
blobs_large, labels_large = make_blobs(int(1e6), 3)
from sklearn.cluster import KMeans, MiniBatchKMeans
kmeans = KMeans(n_clusters=3)
minibatch = MiniBatchKMeans(n_clusters=3)

import datetime
starttime = datetime.datetime.now()
kmeans.fit(blobs_large)
endtime = datetime.datetime.now()
print 'kmeans.fit(blobs_large)'
print (endtime - starttime).seconds

starttime = datetime.datetime.now()
minibatch.fit(blobs_large)
endtime = datetime.datetime.now()
print 'minibatch.fit(blobs_large)'

print (endtime - starttime).seconds


# 我们可能要问的下一个问题就是，两个形心距离多远。
from sklearn.metrics import pairwise
print('两个形心距离')
print pairwise.pairwise_distances(kmeans.cluster_centers_[0],minibatch.cluster_centers_[0])

print('对角线包含形心的差异')
print  np.diag(pairwise.pairwise_distances(kmeans.cluster_centers_,
                                    minibatch.cluster_centers_))

#3. 寻找特征空间中的最接近对象
# Scikit-learn 中，有个叫做 sklearn.metrics.pairwise 的底层工具。
# 它包含一些服务函数，计算矩阵 X 中向量之间的距离，或者 X 和 Y 中的向量距离。
# 这对于信息检索来说很实用。例如，提供一组客户信息，带有属性 X ，
# 我们可能希望选取有个客户代表，并找到与这个客户最接近的客户。
# 实际上，我们可能希望将客户按照相似性度量的概念，使用距离函数来排序。
# 相似性的质量取决于特征空间选取，以及我们在空间上所做的任何变换。
from sklearn.metrics import pairwise
print('检查距离的最简单方式')
distances = pairwise.pairwise_distances(blobs)
# print distances
# distances 是个 NxN 的矩阵，对角线为 0。
# 在最简单的情况中，让我们先看看每个点到第一个点的距离：
print(np.diag(distances) [:5])
print(distances[0][:5])

# 将点按照接近程度排序，很容易使用 np.argsort 做到：
ranks = np.argsort(distances[0])
print('ranks[:5]')
print(ranks[:5])
# argsort 的好处是，现在我们可以排序我们的 points 矩阵，来获得真实的点。

rgb = np.array(['r', 'g', 'b'])
f, ax = plt.subplots(figsize=(7.5, 7.5),nrows=4)
ax[0].scatter(blobs[:, 0], blobs[:, 1], color=rgb[classes])
ax[0].scatter(kmean.cluster_centers_[:, 0],
           kmean.cluster_centers_[:, 1], marker='*', s=250,
           color='black', label='Centers')
ax[0].set_title("Blobs")

ax[1].set_title("Hist of Silhouette Samples")
ax[1].hist(silhouette_samples)

ax[2].plot(sillhouette_avgs)

colors = ['r', 'g', 'b']
for i in range(3):
    p = blobs[classes == i]
    ax[3].scatter(p[:,0], p[:,1], c=colors[i],
               label="Cluster {}".format(i))
    # 簇的形心
    ax[3].scatter(kmean.cluster_centers_[:, 0],
               kmean.cluster_centers_[:, 1], s=100,
               color='black',
               label='Centers')
ax[3].set_title("Cluster With Ground Truth")
ax[3].legend()
plt.show()