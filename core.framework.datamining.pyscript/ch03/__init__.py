import trees
import treePlotter
dataSet, labels=trees.createDataSet()

id3tree=trees.createTree(dataSet,labels)

print(id3tree)