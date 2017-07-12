#coding=utf-8

from numpy import *

#general function to parse tab -delimited floats
def loadDataSet(fileName):
    #assume last column is target value
    dataMat = []
    fr = open(fileName)
    for line in fr.readlines():
        curLine = line.strip().split('\t')
        #map all elements to float()
        fltLine = map(float,curLine)
        dataMat.append(fltLine)
    return dataMat

def distEclud(vecA, vecB):
    #la.norm(vecA-vecB)
    return sqrt(sum(power(vecA - vecB, 2)))

'''
数据集示例
1.658985	4.285136
-3.453687	3.424321
4.838138	-1.151539
-5.379713	-3.362104
0.972564	2.924086
'''
#k为含有簇的数量
def randCent(dataSet, k):
    n = shape(dataSet)[1]#获取矩阵的列数，即特征空间数量
    #create centroid mat
    #簇的中心点
    centroids = mat(zeros((k,n)))
    #create random cluster centers, within bounds of each dimension
    for j in range(n):#按照列进行取值
        minJ = min(dataSet[:,j]) #取j列的最小值，训练样本大小为80，minJ:<type 'tuple'>: (80L, 1L),minJ值为[[-5.379713]]
        rangeJ = float(max(dataSet[:,j]) - minJ)#rangeJ:10.217851
        kRand=random.rand(k,1)#
        # [[ 0.99620998]
        # [ 0.80585781]
        # [ 0.68264915]
        # [ 0.21985956]]
        centroids[:,j] = mat(minJ + rangeJ * kRand)
        # centroids[:,j] = mat(minJ + rangeJ * random.rand(k,1))
    # [[-3.23153828 -2.93204166]
    #  [ 2.97183132 -2.94459474]
    #  [-3.29362306 -1.68867758]
    #  [ 4.15483097  0.38795627]]k*n阶矩阵
    return centroids
    
def kMeans(dataSet, k, distMeas=distEclud, createCent=randCent):
    m = shape(dataSet)[0]#数据训练级的大小，m:80
    #create mat to assign data points
    #to a centroid, also holds SE of each point
    #簇分别矩阵，包含两列，每列索引对应数据训练集的索引。
    # 第一列簇索引值，二列为误差，当前点到簇质心的距离
    #初始化为(0)m*2的矩阵
    clusterAssment = mat(zeros((m,2)))

    centroids = createCent(dataSet, k)
    clusterChanged = True
    while clusterChanged:
        clusterChanged = False
        #for each data point assign it to the closest centroid
        #m为训练样本的数据大小
        for i in range(m):
            minDist = inf; minIndex = -1
            #寻找最近的质心，K个簇中心
            #每个节点
            for j in range(k):
                #计算每个训练到质心的距离，dataSet[i,:]为取一行样本数据，:[[ 1.658985  4.285136]]1*2
                distJI = distMeas(centroids[j,:],dataSet[i,:])
                if distJI < minDist:
                    minDist = distJI;
                    minIndex = j#簇的最小索引
            #训练数据的索引
            if clusterAssment[i,0] != minIndex:
                clusterChanged = True
            #第i行的数据赋值
            #运行后的值
            # 00 = {matrix} [[ 3.         3.0637395]]
            # 01 = {matrix} [[ 2.          1.51252679]]
            #
            # 78 = {matrix} [[ 0.         9.4425941]]
            # 79 = {matrix} [[  1.          41.50303253]]
            clusterAssment[i,:] = minIndex,minDist**2
        print centroids
        #recalculate centroids
        #遍历簇的所有数据
        for cent in range(k):
            #get all the point in this cluster
            #获取clusterAssment第一个属性，clusterAssment的第一列为K，clusterData
            # clusterAssment[:,0]获取第0列，80*1

            # <type 'tuple'>: (array([ 6, 10, 14, 15, 18, 22, 26, 30, 38, 42, 46, 50, 54, 58, 62, 66, 70], dtype=int64),
            #                  array([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], dtype=int64))
            clusterData=nonzero(clusterAssment[:,0].A==cent) #.A转为Array，List
            ptsInClust = dataSet[clusterData[0]]


            # [[-5.379713 -3.362104]
            #  [-0.39237  -3.963704]
            #  [-4.007257 -3.207066]
            #  [-2.579316 -3.497576]
            #  [-3.837877 -3.253815]
            #  [-2.121479 -4.232586]
            #  [-4.323818 -3.938116]
            #  [-3.171184 -3.572452]
            #  [-4.905566 -2.91107 ]]
            # ptsInClust = dataSet[nonzero(clusterAssment[:,0].A==cent)[0]]
            #assign centroid to mean #axis=0表示矩阵按照列取均值
            centroids[cent,:] = mean(ptsInClust, axis=0)
    return centroids, clusterAssment

'''二分法K-均值算法'''
def biKmeans(dataSet, k, distMeas=distEclud):
    m = shape(dataSet)[0]
    clusterAssment = mat(zeros((m,2)))
    centroid0 = mean(dataSet, axis=0).tolist()[0]
    #create a list with one centroid
    centList =[centroid0]
    #calc initial Error
    for j in range(m):
        clusterAssment[j,1] = distMeas(mat(centroid0), dataSet[j,:])**2
    while (len(centList) < k):
        lowestSSE = inf
        for i in range(len(centList)):
            #get the data points currently in cluster i
            ptsInCurrCluster = dataSet[nonzero(clusterAssment[:,0].A==i)[0],:]
            centroidMat, splitClustAss = kMeans(ptsInCurrCluster, 2, distMeas)
            #compare the SSE to the currrent minimum
            sseSplit = sum(splitClustAss[:,1])
            sseNotSplit = sum(clusterAssment[nonzero(clusterAssment[:,0].A!=i)[0],1])
            print "sseSplit, and notSplit: ",sseSplit,sseNotSplit
            if (sseSplit + sseNotSplit) < lowestSSE:
                bestCentToSplit = i
                bestNewCents = centroidMat
                bestClustAss = splitClustAss.copy()
                lowestSSE = sseSplit + sseNotSplit
        #change 1 to 3,4, or whatever
        bestClustAss[nonzero(bestClustAss[:,0].A == 1)[0],0] = len(centList)
        bestClustAss[nonzero(bestClustAss[:,0].A == 0)[0],0] = bestCentToSplit
        print 'the bestCentToSplit is: ',bestCentToSplit
        print 'the len of bestClustAss is: ', len(bestClustAss)
        #replace a centroid with two best centroids
        centList[bestCentToSplit] = bestNewCents[0,:].tolist()[0]
        centList.append(bestNewCents[1,:].tolist()[0])
        #reassign new clusters, and SSE
        clusterAssment[nonzero(clusterAssment[:,0].A == bestCentToSplit)[0],:]= bestClustAss
    return mat(centList), clusterAssment

'''
====================
'''
# import urllib
# import json
# def geoGrab(stAddress, city):
#     #create a dict and constants for the goecoder
#     apiStem = 'http://where.yahooapis.com/geocode?'
#     params = {}
#     params['flags'] = 'J'#JSON return type
#     params['appid'] = 'aaa0VN6k'
#     params['location'] = '%s %s' % (stAddress, city)
#     url_params = urllib.urlencode(params)
#     yahooApi = apiStem + url_params      #print url_params
#     print yahooApi
#     c=urllib.urlopen(yahooApi)
#     return json.loads(c.read())

# from time import sleep
# def massPlaceFind(fileName):
#     fw = open('places.txt', 'w')
#     for line in open(fileName).readlines():
#         line = line.strip()
#         lineArr = line.split('\t')
#         retDict = geoGrab(lineArr[1], lineArr[2])
#         if retDict['ResultSet']['Error'] == 0:
#             lat = float(retDict['ResultSet']['Results'][0]['latitude'])
#             lng = float(retDict['ResultSet']['Results'][0]['longitude'])
#             print "%s\t%f\t%f" % (lineArr[0], lat, lng)
#             fw.write('%s\t%f\t%f\n' % (line, lat, lng))
#         else: print "error fetching"
#         sleep(1)
#     fw.close()

def distSLC(vecA, vecB):#Spherical Law of Cosines
    a = sin(vecA[0,1]*pi/180) * sin(vecB[0,1]*pi/180)
    b = cos(vecA[0,1]*pi/180) * cos(vecB[0,1]*pi/180) * \
                      cos(pi * (vecB[0,0]-vecA[0,0]) /180)
    return arccos(a + b)*6371.0 #pi is imported with numpy


import matplotlib
import matplotlib.pyplot as plt
def clusterClubs(numClust=5):
    datList = []
    for line in open('places.txt').readlines():
        lineArr = line.split('\t')
        datList.append([float(lineArr[4]), float(lineArr[3])])
    datMat = mat(datList)
    myCentroids, clustAssing = biKmeans(datMat, numClust, distMeas=distSLC)
    fig = plt.figure()
    rect=[0.1,0.1,0.8,0.8]
    scatterMarkers=['s', 'o', '^', '8', 'p', \
                    'd', 'v', 'h', '>', '<']
    axprops = dict(xticks=[], yticks=[])
    ax0=fig.add_axes(rect, label='ax0', **axprops)
    imgP = plt.imread('Portland.png')
    ax0.imshow(imgP)
    ax1=fig.add_axes(rect, label='ax1', frameon=False)
    for i in range(numClust):
        ptsInCurrCluster = datMat[nonzero(clustAssing[:,0].A==i)[0],:]
        markerStyle = scatterMarkers[i % len(scatterMarkers)]
        ax1.scatter(ptsInCurrCluster[:,0].flatten().A[0], ptsInCurrCluster[:,1].flatten().A[0], marker=markerStyle, s=90)
    ax1.scatter(myCentroids[:,0].flatten().A[0], myCentroids[:,1].flatten().A[0], marker='+', s=300)
    plt.show()
