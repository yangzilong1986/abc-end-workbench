import kNN
if __name__ == '__main__':
    group ,labels=kNN.createDataSet()

    cf=kNN.classify0([0,0],group,labels,1)

    print(cf)