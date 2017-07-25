#coding=utf-8
import cv2
import numpy as np
from matplotlib import pyplot as plt

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
img1 = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)
img2 =cv2.imread('D:\\DevN\\sample-data\\images\\football\\blending.jpg',0)

# ret, thresh = cv2.threshold(img1, 127, 255,0)
# ret, thresh2 = cv2.threshold(img2, 127, 255,0)
# contours,hierarchy = cv2.findContours(thresh,2,1)
# cnt1 = contours[0]
# contours,hierarchy = cv2.findContours(thresh2,2,1)
# cnt2 = contours[0]
# ret = cv2.matchShapes(cnt1,cnt2,1,0.0)
# print ret

# 别忘了中括号[img],[0],None,[256],[0,256]，只有mask 没有中括号
# hist = cv2.calcHist([img],[0],None,[256],[0,256])

# plt.hist(img.ravel(),256,[0,256]);
# plt.show()

color = ('b','g','r')
# 对一个列表或数组既要遍历索引又要遍历元素时
# 使用内置enumerrate 函数会有更加直接，优美的做法
#enumerate 会将数组或列表组成一个索引序列。
# 使我们再获取索引和索引内容的时候更加方便
for i,col in enumerate(color):
    histr = cv2.calcHist([img],[i],None,[256],[0,256])
    plt.plot(histr,color = col)
    plt.xlim([0,256])
plt.show()