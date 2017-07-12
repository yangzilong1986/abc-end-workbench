#coding=utf-8
import numpy as np
import cv2

# 警告：所有的绘图函数的返回值都是None， 所以不能使用img =
# cv2.line(img,(0,0),(511,511),(255,0,0),5)。
# 函数：cv2.line()，cv2.circle()，cv2.rectangle()，
# cv2.ellipse()，cv2.putText() 等。
# 上面所有的这些绘图函数需要设置下面这些参数：
# img：你想要绘制图形的那幅图像。

# color：形状的颜色。以RGB 为例，需要传入一个元组，
# 例如：（255,0,0）代表蓝色。对于灰度图只需要传入灰度值。
# thickness：线条的粗细。如果给一个闭合图形设置为-1，那么这个图形
# 就会被填充。默认值是1.
#linetype：线条的类型，8 连接，抗锯齿等。默认情况是8 连接。cv2.LINE_AA
#为抗锯齿，这样看起来会非常平滑。

# Create a black image
img = np.zeros((512,512,3), np.uint8)

# Draw a diagonal blue line with thickness of 5 px
#openvc-3
# img = cv2.line(img,(0,0),(511,511),(255,0,0),5)
#vc2
#要画一条线，你只需要告诉函数这条线的起点和终点。我们下面会画一条
#从左上方到右下角的蓝色线段。
cv2.line(img,(0,0),(511,511),(255,0,0),5)

#要画一个矩形，你需要告诉函数的左上角顶点和右下角顶点的坐标。这次
#我们会在图像的右上角话一个绿色的矩形。
cv2.rectangle(img,(384,0),(510,128),(0,255,0),3)

# 要画圆的话，只需要指定圆形的中心点坐标和半径大小。我们在上面的矩
# 形中画一个圆。
cv2.circle(img,(447,63), 63, (0,0,255), -1)

# 画椭圆比较复杂，我们要多输入几个参数。
# 一个参数是中心点的位置坐标。
# 下一个参数是长轴和短轴的长度。椭圆沿逆时针方向旋转的角度。椭圆弧演
# 顺时针方向起始的角度和结束角度，如果是0 很360，就是整个椭圆。
#下面的例子是在图片的中心绘制半个椭圆。
cv2.ellipse(img,(256,256),(100,50),0,0,180,255,-1)

pts=np.array([[100,5],[20,30],[70,20],[50,10]], np.int32)
# 这里reshape 的第一个参数为-1, 表明这一维的长度是根据后面的维度的计算出来的。
pts=pts.reshape((-1,1,2))
pts = np.array([[10,5],[20,30],[70,20],[50,10]], np.int32)
pts = pts.reshape((-1,1,2))
#注意下面的[]
cv2.polylines(img,[pts],True,(0,0,255))

font=cv2.FONT_HERSHEY_SIMPLEX
cv2.putText(img,'OpenCV',(10,500), font, 4,(255,255,255),2)

#显示图像
cv2.imshow('image',img)

cv2.waitKey(0)
cv2.destroyAllWindows()