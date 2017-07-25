#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

# img = cv2.imread('D:\\DevN\\sample-data\\images\\wiki.jpg',0)
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)
#flatten() 将数组变成一维
hist,bins = np.histogram(img.flatten(),256,[0,256])
# 计算累积分布图
cdf = hist.cumsum()
cdf_normalized = cdf * hist.max()/ cdf.max()

equ = cv2.equalizeHist(img)
res = np.hstack((img,equ))

# create a CLAHE object (Arguments are optional).
# 不知道为什么我没好到createCLAHE 这个模块
clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8,8))
cl1 = clahe.apply(img)

cv2.imshow('img',res )
cv2.waitKey(0)
cv2.destroyAllWindows()

# plt.plot(cdf_normalized, color = 'b')
# plt.hist(img.flatten(),256,[0,256], color = 'r')
# plt.xlim([0,256])
# plt.legend(('cdf','histogram'), loc = 'upper left')
# plt.show()


#stacking images side-by-side
# cv2.imwrite('res.png',res)