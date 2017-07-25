#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

#仿射变换
# 原图中所有的平行线在结果图像中同样平行。为了创建这
# 个矩阵我们需要从原图像中找到三个点以及他们在输出图像中的位置。
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
rows,cols,ch = img.shape
#
# 在仿射变换中，原图中所有的平行线在结果图像中同样平行。为了创建这
# 个矩阵我们需要从原图像中找到三个点以及他们在输出图像中的位置。然后
# cv2.getAffineTransform 会创建一个2x3 的矩阵，最后这个矩阵会被传给
# 函数cv2.warpAffine。

# pts1 = np.float32([[50,50],[200,50],[50,200]])
# pts2 = np.float32([[10,100],[200,50],[100,250]])
#
# M = cv2.getAffineTransform(pts1,pts2)
#
# dst = cv2.warpAffine(img,M,(cols,rows))

########################################
# 对于视角变换，我们需要一个3x3 变换矩阵.
# 在变换前后直线还是直线。
# 要构建这个变换矩阵，你需要在输入图像上找4 个点，以及他们在输出图
# 像上对应的位置。这四个点中的任意三个都不能共线。这个变换矩阵可以有
# 函数cv2.getPerspectiveTransform() 构建。然后把这个矩阵传给函数
# cv2.warpPerspective

pts1 = np.float32([[56,65],[368,52],[28,387],[389,390]])
pts2 = np.float32([[0,0],[300,0],[0,300],[300,300]])
M=cv2.getPerspectiveTransform(pts1,pts2)
dst=cv2.warpPerspective(img,M,(300,300))

plt.subplot(121),plt.imshow(img),plt.title('Input')
plt.subplot(122),plt.imshow(dst),plt.title('Output')
plt.show()