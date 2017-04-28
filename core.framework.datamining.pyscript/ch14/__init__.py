#coding=utf-8
import svdRec
from numpy import *
if __name__ == '__main__':
    datSet=svdRec.loadExData()
    U,Signam,VT=linalg.svd(datSet)
    print(Signam)
