#coding=utf-8
#图像进行各种几个变换，例如移动，旋转，仿射变换等
# 变换
#OpenCV 提供了两个变换函数，
# cv2.warpAffine 和cv2.warpPerspective，
#使用这两个函数你可以实现所有类型的变换
#cv2.warpAffine接收的参数是2*3矩阵
#cv2.warpPerspective 接收的参数是3*3矩阵

#缩放
# 扩展缩放只是改变图像的尺寸大小。OpenCV 提供的函数cv2.resize()
# 可以实现这个功能。图像的尺寸可以自己手动设置，你也可以指定缩放因子。

# 可以选择使用不同的插值方法。在缩放时我们推荐使用cv2.INTER_AREA，
# 在扩展时我们推荐使用v2.INTER_CUBIC（慢) 和v2.INTER_LINEAR。
# 默认情况下所有改变图像尺寸大小的操作使用的插值方法都是cv2.INTER_LINEAR。
# 你可以使用下面任意一种方法改变图像的尺寸：
import cv2
import numpy as np
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',3)


#################################################################################
##########   Resize(src, dst, interpolation=CV_INTER_LINEAR)         ############
#################################################################################

#下面的None本应该是输出图像的尺寸，但是因为后边我们设置了缩放因子
res=cv2.resize(img,None,fx=2,fy=2,interpolation=cv2.INTER_CUBIC)

#这里呢，我们直接设置输出图像的尺寸，所以不用设置缩放因子
# height,width=img.shape[:2]
# res=cv2.resize(img,(480,360),interpolation=cv2.INTER_CUBIC)

#################################################################################
#平移
# 平移就是将对象换一个位置。如果你要沿（x，y）方向移动，移动的距离
# 是（tx，ty），你可以以下面的方式构建移动矩阵：
#可以使用Numpy 数组构建这个矩阵（数据类型是np.float32）
#然后把它传给函数cv2.warpAffine()

# 看看下面这个例子吧，它被移动了（100,50）个像素。

while(1):
    cv2.imshow('res',res)
    cv2.imshow('img',img)
    if cv2.waitKey(1) & 0xFF == 27:
        break
cv2.destroyAllWindows()