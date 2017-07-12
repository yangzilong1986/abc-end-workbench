#coding=utf-8
import numpy as np
import cv2

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
cv2.imshow('image',img)
cv2.waitKey(0)
cv2.destroyAllWindows()