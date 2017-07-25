#coding=utf-8
import cv2
import numpy as np
from matplotlib import pyplot as plt

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
img1 = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
img2 = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5-3.jpg',3)

# # Initiate STAR detector
# orb = cv2.ORB()
#
# # find the keypoints with ORB
# kp = orb.detect(img,None)
#
# # compute the descriptors with ORB
# kp, des = orb.compute(img, kp)
#
# # draw only keypoints location,not size and orientation
# img2 = cv2.drawKeypoints(img,kp,color=(0,255,0), flags=0)
# plt.imshow(img2),plt.show()

# Initiate SIFT detector
sift = cv2.xfeatures2d.SIFT_create()
# find the keypoints and descriptors with SIFT
kp1, des1 = sift.detectAndCompute(img1,None)
kp2, des2 = sift.detectAndCompute(img2,None)
# BFMatcher with default params
bf = cv2.BFMatcher()
matches = bf.knnMatch(des1,des2, k=2)
# Apply ratio test
# 比值测试，首先获取与A 距离最近的点B（最近）和C（次近），只有当B/C
# 小于阈值时（0.75）才被认为是匹配，因为假设匹配是一一对应的，真正的匹配的理想距离为0
good = []
for m,n in matches:
    if m.distance < 0.75*n.distance:
        good.append([m])

print(good)

# img3 = cv2.drawKeypoints(img1,kp1,img2,kp2,matches[:10])

# cv2.drawMatchesKnn expects list of lists as matches.
img3 = cv2.drawMatchesKnn(img1,kp1,img2,kp2,good[:10],flags=2)
plt.imshow(img3),plt.show()