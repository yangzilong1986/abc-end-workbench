#coding=utf-8

import cv2
import numpy as np

img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)
print(img.shape)
rows,cols,chanels = img.shape
#################################################################################
#平移
# 平移就是将对象换一个位置。如果你要沿（x，y）方向移动，移动的距离
# 是（tx，ty），你可以以下面的方式构建移动矩阵：
'''

    1 0 tx
M=
    0 1 ty
'''
#可以使用Numpy 数组构建这个矩阵（数据类型是np.float32）
#然后把它传给函数cv2.warpAffine()

# 看看下面这个例子吧，它被移动了（100,50）个像素。
# 警告：
# 函数cv2.warpAffine() 的第三个参数的是输出图像的大小，它的格式
# 应该是图像的（宽，高）。
# 应该记住的是图像的宽对应的是列数，高对应的是行数。
M = np.float32([[1,0,100],[0,1,50]])
dst = cv2.warpAffine(img,M,(cols,rows))


cv2.imshow('img',dst)
cv2.waitKey(0)
cv2.destroyAllWindows()