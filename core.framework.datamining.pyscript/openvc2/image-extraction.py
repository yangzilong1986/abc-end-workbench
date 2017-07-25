#coding=utf-8
import cv2
import numpy as np
from matplotlib import pyplot as plt

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
newmask =  cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)

mask = np.zeros(img.shape[:2],np.uint8)
bgdModel = np.zeros((1,65),np.float64)#
fgdModel = np.zeros((1,65),np.float64)#
rect = (50,50,450,290)
# cv2.grabCut()
# 函数的返回值是更新的mask, bgdModel, fgdModel
# mask-掩模图像，用来确定那些区域是背景，前景，可能是前景/背景等
# mode :cv2.GC_INIT_WITH_RECT 或者cv2.GC_INIT_WITH_MASK
# 矩形模式，或者掩模
cv2.grabCut(img,mask,rect,bgdModel,fgdModel,5,cv2.GC_INIT_WITH_RECT)
mask2 = np.where((mask==2)|(mask==0),0,1).astype('uint8')

#
# # whereever it is marked white (sure foreground), change mask=1
# # whereever it is marked black (sure background), change mask=0
# mask[newmask == 0] = 0
# mask[newmask == 255] = 1
# mask, bgdModel, fgdModel = cv2.grabCut(img,mask,None,bgdModel,fgdModel,5,cv2.GC_INIT_WITH_MASK)
mask = np.where((mask==2)|(mask==0),0,1).astype('uint8')
img = img*mask[:,:,np.newaxis]
plt.imshow(img),plt.colorbar(),plt.show()

