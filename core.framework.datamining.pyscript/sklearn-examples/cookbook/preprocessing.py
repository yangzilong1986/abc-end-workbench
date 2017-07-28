#encoding:utf-8
from sklearn import datasets
import numpy as np
from sklearn import preprocessing
from sklearn import pipeline
from sklearn import decomposition
from matplotlib import pyplot as plt

from sklearn.decomposition import KernelPCA

iris = datasets.load_iris()
#iris元数据包括分类列
# X = iris.data
# y = iris.target
# print (X)
# print (y)
#
# d = np.column_stack((X, y))
#
# text_encoder = preprocessing.OneHotEncoder()
# c=text_encoder.fit_transform(d[:, -1:]).toarray()
# print (d)
#
iris_X=iris.data
# masking_array = np.random.binomial(1, .25, iris_X.shape).astype(bool)
#
# iris_X[masking_array] = np.nan
#
# print (masking_array)
#
# print (iris_X)
#
impute = preprocessing.Imputer()
# scaler = preprocessing.StandardScaler()
# pipe = pipeline.Pipeline([('impute', impute), ('scaler', scaler)
#                           ])
# new_mat = pipe.fit_transform(iris_X)
# print new_mat[:4, :4];

pca = decomposition.PCA(n_components=2)
iris_X_prime = pca.fit_transform(iris_X)

print(iris_X_prime.shape)

iris_pca = pca.fit_transform(iris_X)
print (iris_pca[:5])
print (pca.explained_variance_ratio_)

f = plt.figure(figsize=(5, 5))
ax = f.add_subplot(111)
ax.scatter(iris_X_prime[:,0], iris_X_prime[:, 1], c=iris.target)
ax.set_title("PCA 2 Components")
plt.show()

from sklearn.decomposition import KernelPCA
kpca = KernelPCA(kernel='cosine', n_components=1)
# AB = np.vstack((A, B))
AB_transformed = kpca.fit_transform(iris_X_prime)
