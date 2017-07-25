#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

#仿射变换
# 原图中所有的平行线在结果图像中同样平行。为了创建这
# 个矩阵我们需要从原图像中找到三个点以及他们在输出图像中的位置。
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
blur = cv2.blur(img,(15,15))

#0是指根据窗口大小（5,5）来计算高斯函数标准差
blur = cv2.GaussianBlur(blur,(65,65),0)
# 中值模糊是用与卷积框对应像素的中值来替代中心像素的值
# 这个滤波器经常用来去除椒盐噪声。前面的滤波器都是用计算得到的一个新值来取代中
# 心像素的值，而中值滤波是用中心像素周围（也可以使他本身）的值来取代他。
# 他能有效的去除噪声。卷积核的大小也应该是一个奇数。
# 我们给原始图像加上50% 的噪声然后再使用中值模糊。
blur = cv2.medianBlur(blur,5)

#9邻域直径，两个75分别是空间高斯函数标准差，灰度值相似性高斯函数标准差
blur = cv2.bilateralFilter(img,9,175,175)

plt.subplot(121),plt.imshow(img),plt.title('Original')
plt.xticks([]), plt.yticks([])
plt.subplot(122),plt.imshow(blur),plt.title('Blurred')
plt.xticks([]), plt.yticks([])
plt.show()