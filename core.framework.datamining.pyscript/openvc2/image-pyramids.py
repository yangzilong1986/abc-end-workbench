#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt

apple = cv2.imread('D:\\DevN\\sample-data\\images\\apple.jpg',3)
orange = cv2.imread('D:\\DevN\\sample-data\\images\\orange.jpg',3)
lower_reso = cv2.pyrDown(apple)
lower_reso = cv2.pyrDown(lower_reso)
lower_reso = cv2.pyrDown(lower_reso)
higher_reso2 = cv2.pyrUp(lower_reso)
higher_reso2 = cv2.pyrUp(higher_reso2)
higher_reso2 = cv2.pyrUp(higher_reso2)
cv2.imshow('img',higher_reso2)
cv2.waitKey(0)
cv2.destroyAllWindows()
#
# A = cv2.resize(apple,(200,200),interpolation=cv2.INTER_CUBIC)
# B = cv2.resize(orange,(200,200),interpolation=cv2.INTER_CUBIC)
# G = A.copy()
# gpA = [G]
#
# for i in xrange(6):
#     G = cv2.pyrDown(G)
#     gpA.append(G)
# # generate Gaussian pyramid for B
# G = B.copy()
# gpB = [G]
# for i in xrange(6):
#     G = cv2.pyrDown(G)
#     gpB.append(G)
# # generate Laplacian Pyramid for A
# lpA = [gpA[5]]
# for i in xrange(5,0,-1):
#     GE = cv2.pyrUp(gpA[i])
#     L = cv2.subtract(gpA[i-1],GE)
#     lpA.append(L)
# # generate Laplacian Pyramid for B
# lpB = [gpB[5]]
# for i in xrange(5,0,-1):
#     GE = cv2.pyrUp(gpB[i])
#     L = cv2.subtract(gpB[i-1],GE)
#     lpB.append(L)
# # Now add left and right halves of images in each level
# #numpy.hstack(tup)
# #Take a sequence of arrays and stack them horizontally
# #to make a single array.
# LS = []
# for la,lb in zip(lpA,lpB):
#     rows,cols,dpt = la.shape
#     ls = np.hstack((la[:,0:cols/2], lb[:,cols/2:]))
#     LS.append(ls)
# # now reconstruct
# ls_ = LS[0]
# for i in xrange(1,6):
#     ls_ = cv2.pyrUp(ls_)
#     ls_ = cv2.add(ls_, LS[i])
# # image with direct connecting each half
# real = np.hstack((A[:,:cols/2],B[:,cols/2:]))
# cv2.imwrite('Pyramid_blending2.jpg',ls_)
# cv2.imwrite('Direct_blending.jpg',real)
