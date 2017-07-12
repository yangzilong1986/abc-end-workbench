#coding=utf-8
import cv2
import numpy as np
from matplotlib import pyplot as plt

#函数：cv2.add()，cv2.addWeighted() 等。
# 注意：OpenCV 中的加法与Numpy的加法是有所不同的。
# OpenCV 的加法是一种饱和操作，而Numpy的加法是一种模操作。

#可以使用函数cv2.add() 将两幅图像进行加法运算，
# 当然也可以直接使用numpy，res=img1+img。两幅图像的大小，类型必须一致，
# 或者第二个图像可以使一个简单的标量值。
x = np.uint8([250])
y = np.uint8([10])
print cv2.add(x,y)## 250+10 = 260 => 255

print x+y# 250+10 = 260 % 256 = 4

# img1=cv2.imread('ml.png')
# <type 'tuple'>: (342L, 548L, 3L)
img1 = cv2.imread('D:\\DevN\\sample-data\\images\\size-100\\calib3d_icon.jpg')
# <type 'tuple'>: (616L, 500L, 3L)
img2 = cv2.imread('D:\\DevN\\sample-data\\images\\size-100\\featureicon.jpg')

dst=cv2.addWeighted(img1,0.7,img2,0.3,0)

cv2.imshow('dst',dst)
cv2.waitKey(0)
# cv2.destroyAllWindow()