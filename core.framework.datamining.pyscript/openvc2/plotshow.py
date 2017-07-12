
import numpy as np
import cv2
from matplotlib import pyplot as plt
img = cv2.imread('D:\\DevN\\sample-data\\images\\football\\messi5.jpg',0)

#img2=cv2.resize(img,[100,100])

plt.imshow(img, cmap = 'gray', interpolation = 'bicubic')
plt.xticks([]), plt.yticks([])  # to hide tick values on X and Y axis
plt.show()