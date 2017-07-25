#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

# img = cv2.imread('gradient.png',0)
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)
# 简单阈值，自适应阈值，Otsu’s 二值化等
# 函数有cv2.threshold，cv2.adaptiveThreshold 等。
# 这种方法非常简单。但像素值高于阈值时，我们给这个像素
# 赋予一个新值（可能是白色），否则我们给它赋予另外一种颜色（也许是黑色）。
# 这个函数就是cv2.threshhold()。这个函数的第一个参数就是原图像，原图
# 像应该是灰度图。第二个参数就是用来对像素值进行分类的阈值。第三个参数
# 就是当像素值高于（有时是小于）阈值时应该被赋予的新的像素值。OpenCV
# 提供了多种不同的阈值方法，这是有第四个参数来决定的。

ret,thresh1 = cv2.threshold(img,127,255,cv2.THRESH_BINARY)
ret,thresh2 = cv2.threshold(img,127,255,cv2.THRESH_BINARY_INV)
ret,thresh3 = cv2.threshold(img,127,255,cv2.THRESH_TRUNC)
ret,thresh4 = cv2.threshold(img,127,255,cv2.THRESH_TOZERO)
ret,thresh5 = cv2.threshold(img,127,255,cv2.THRESH_TOZERO_INV)

titles = ['Original Image','BINARY','BINARY_INV','TRUNC','TOZERO','TOZERO_INV']
images = [img, thresh1, thresh2, thresh3, thresh4, thresh5]

for i in xrange(6):
    plt.subplot(2,3,i+1),plt.imshow(images[i],'gray')
    plt.title(titles[i])
    plt.xticks([]),plt.yticks([])

plt.show()