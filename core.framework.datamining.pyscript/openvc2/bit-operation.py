#coding=utf-8
import cv2
import numpy as np

imgs1 = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg')
imgs2 = cv2.imread('D:\\DevN\\sample-data\\images\\openvc.png')
#修改为统一的大小之后，可以合并
img1 = cv2.resize(imgs1,(480,360),interpolation=cv2.INTER_CUBIC)
img2 = cv2.resize(imgs2,(480,360),interpolation=cv2.INTER_CUBIC)

# 这里包括的按位操作有：AND，OR，NOT，XOR 等。当我们提取图像的一部分，选择非矩形ROI 时这些操作会很有用.

# 把OpenCV 的标志放到另一幅图像上。如果我使用加法，颜色会改变，如果使用混合，会得到透明效果，
# 但是我不想要透明。如果他是矩形我可以象上一章那样使用ROI。但是他不是矩形。但是我们可以通过下面的按位运算实现：
# I want to put logo on top-left corner, So I create a ROI
rows,cols,channels = img2.shape
roi = img1[0:rows, 0:cols ]
# Now create a mask of logo and create its inverse mask also
img2gray = cv2.cvtColor(img2,cv2.COLOR_BGR2GRAY)
ret, mask = cv2.threshold(img2gray, 175, 255, cv2.THRESH_BINARY)
mask_inv = cv2.bitwise_not(mask)
# Now black-out the area of logo in ROI
# 取roi 中与mask 中不为零的值对应的像素的值，其他值为0
# 注意这里必须有mask=mask 或者mask=mask_inv, 其中的mask= 不能忽略
img1_bg = cv2.bitwise_and(roi,roi,mask = mask)
# 取roi 中与mask_inv 中不为零的值对应的像素的值，其他值为0。
# Take only region of logo from logo image.
img2_fg = cv2.bitwise_and(img2,img2,mask = mask_inv)
# Put logo in ROI and modify the main image
dst = cv2.add(img1_bg,img2_fg)
img1[0:rows, 0:cols ] = dst
cv2.imshow('res',img1)
cv2.waitKey(0)
cv2.destroyAllWindows()

