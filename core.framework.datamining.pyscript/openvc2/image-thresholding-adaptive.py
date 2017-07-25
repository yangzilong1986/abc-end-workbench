#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

# img = cv2.imread('gradient.png',0)
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)

#尤其是当同一幅图像上的不同部分的具有不同亮度时。这种情况下我们需要采用自适应阈值。
# 此时的阈值是根据图像上的每一个小区域计算与其对应的阈值。
# 因此在同一幅图像上的不同区域采用的是不同的阈值，
# 从而使我们能在亮度不同的情况下得到更好的结果。

# 中值滤波
img = cv2.medianBlur(img,5)
ret,th1 = cv2.threshold(img,127,255,cv2.THRESH_BINARY)
#11 为Block size, 2 为C 值
th2 = cv2.adaptiveThreshold(img,255,cv2.ADAPTIVE_THRESH_MEAN_C, \
                            cv2.THRESH_BINARY,11,2)
th3 = cv2.adaptiveThreshold(img,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C, \
                            cv2.THRESH_BINARY,11,2)
titles = ['Original Image', 'Global Thresholding (v = 127)',
          'Adaptive Mean Thresholding', 'Adaptive Gaussian Thresholding']
images = [img, th1, th2, th3]

for i in xrange(4):
    plt.subplot(2,2,i+1),plt.imshow(images[i],'gray')
    plt.title(titles[i])
    plt.xticks([]),plt.yticks([])
plt.show()