#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

#仿射变换
# 原图中所有的平行线在结果图像中同样平行。为了创建这
# 个矩阵我们需要从原图像中找到三个点以及他们在输出图像中的位置。
img1 = cv2.imread('D:\\DevN\\sample-data\\images\\star1.png',0)
img2 = cv2.imread('D:\\DevN\\sample-data\\images\\star2.png',0)

ret, thresh = cv2.threshold(img1, 127, 255,0)
ret, thresh2 = cv2.threshold(img2, 127, 255,0)
contours,hierarchy = cv2.findContours(thresh,2,1)
cnt1 = contours[0]
contours,hierarchy = cv2.findContours(thresh2,2,1)
cnt2 = contours[0]

ret = cv2.matchShapes(cnt1,cnt2,1,0.0)
print (ret)