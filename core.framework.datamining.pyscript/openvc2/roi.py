#coding=utf-8
import sys

import numpy as np
import cv2

import freetype
import put_chinese_text

import cv2.cv as cv
from matplotlib import pyplot as plt
BLUE=[255,0,0]

msg = '在OpenCV中输出汉字！'

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
#拷贝，即roi操作
ball = img[280:340, 330:390]
img[273:333, 100:160] = ball

#显示图片
cv2.imshow('image',img)
cv2.waitKey(0)
cv2.destroyAllWindows()