#coding=utf-8
import cv2
import numpy as np
from matplotlib import pyplot as plt

img = cv2.imread('D:\\DevN\\sample-data\\images\\feature_building.jpg',3)
img= cv2.imread('D:\\DevN\\sample-data\\images\\openvc.png')
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)

gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
gray = np.float32(gray)
# cornerHarris说明
# blockSize - 角点检查中要考虑的领域大小
# ksize - Sobel 求导中使用的窗口大小
# 输入图像必须是float32，
# k - Harris 角点检测方程中的自由参数.在0.04 到0.05 之间
dst = cv2.cornerHarris(gray,2,3,0.04)
#result is dilated for marking the corners, not important
dst = cv2.dilate(dst,None)
# Threshold for an optimal value, it may vary depending on the image.
img[dst>0.01*dst.max()]=[0,0,255]


cv2.imshow('image',img)
cv2.waitKey(0)
cv2.destroyAllWindows()

#
# gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
# # find Harris corners
# gray = np.float32(gray)
# dst = cv2.cornerHarris(gray,2,3,0.04)
# dst = cv2.dilate(dst,None)
# ret, dst = cv2.threshold(dst,0.01*dst.max(),255,0)
# dst = np.uint8(dst)
# # find centroids
# #connectedComponentsWithStats(InputArray image, OutputArray labels, OutputArray stats,
# #OutputArray centroids, int connectivity=8, int ltype=CV_32S)
# test=cv2.connectedComponentsWithStats(dst)
# ret, labels, stats, centroids = cv2.connectedComponentsWithStats(dst)
# # define the criteria to stop and refine the corners
# criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 100, 0.001)
# #Python: cv2.cornerSubPix(image, corners, winSize, zeroZone, criteria)
# #zeroZone ¨C Half of the size of the dead region in the middle of the search zone
# #over which the summation in the formula below is not done. It is used sometimes
# # to avoid possible singularities of the autocorrelation matrix. The value of (-1,-1)
# # indicates that there is no such a size.
#
# corners = cv2.cornerSubPix(gray,np.float32(centroids),(5,5),(-1,-1),criteria)
# # Now draw them
# res = np.hstack((centroids,corners))
#
# res = np.int0(res)
# img[res[:,1],res[:,0]]=[0,0,255]
# img[res[:,3],res[:,2]] = [0,255,0]
# cv2.imwrite('subpixel5.png',img)