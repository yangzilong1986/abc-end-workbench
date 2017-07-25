#coding=utf-8

import cv2
import numpy as np
from matplotlib import pyplot as plt
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)
# 首先我们看看如何使用Numpy 进行傅里叶变换。Numpy 中的FFT 包
# 可以帮助我们实现快速傅里叶变换。函数np.fft.fft2() 可以对信号进行频率转
# 换，输出结果是一个复杂的数组。本函数的第一个参数是输入图像，要求是灰
# 度格式。第二个参数是可选的, 决定输出数组的大小。输出数组的大小和输入图
# 像大小一样。如果输出结果比输入图像大，输入图像就需要在进行FFT 前补
# 0。如果输出结果比输入图像小的话，输入图像就会被切割。
f = np.fft.fft2(img)
fshift = np.fft.fftshift(f)
# magnitude_spectrum = 20*np.log(np.abs(fshift))
# plt.subplot(121),plt.imshow(img, cmap = 'gray')
# plt.title('Input Image'), plt.xticks([]), plt.yticks([])
# plt.subplot(122),plt.imshow(magnitude_spectrum, cmap = 'gray')
# plt.title('Magnitude Spectrum'), plt.xticks([]), plt.yticks([])
# plt.show()

rows, cols = img.shape
crow,ccol = rows/2 , cols/2
fshift[crow-30:crow+30, ccol-30:ccol+30] = 0
f_ishift = np.fft.ifftshift(fshift)
img_back = np.fft.ifft2(f_ishift)
# 取绝对值
img_back = np.abs(img_back)
plt.subplot(131),plt.imshow(img, cmap = 'gray')
plt.title('Input Image'), plt.xticks([]), plt.yticks([])
plt.subplot(132),plt.imshow(img_back, cmap = 'gray')
plt.title('Image after HPF'), plt.xticks([]), plt.yticks([])
plt.subplot(133),plt.imshow(img_back)
plt.title('Result in JET'), plt.xticks([]), plt.yticks([])
plt.show()