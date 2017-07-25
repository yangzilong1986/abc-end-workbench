#coding=utf-8

import cv2
import numpy as np

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
print(img.shape)
rows,cols,chanels = img.shape

# 为了构建这个旋转矩阵，OpenCV 提供了一个函数：cv2.getRotationMatrix2D。
# 下面的例子是在不缩放的情况下将图像旋转90 度。
# M = cv2.getRotationMatrix2D((cols/2,rows/2),40,1)
# dst = cv2.warpAffine(img,M,(cols,rows))

# 这里的第一个参数为旋转中心，第二个为旋转角度，第三个为旋转后的缩放因子
# 可以通过设置旋转中心，缩放因子，以及窗口大小来防止旋转后超出边界的问题
M=cv2.getRotationMatrix2D((cols/2,rows/2),45,0.6)
# 第三个参数是输出图像的尺寸中心
dst=cv2.warpAffine(img,M,(2*cols,2*rows))


cv2.imshow('img',dst)
cv2.waitKey(0)
cv2.destroyAllWindows()