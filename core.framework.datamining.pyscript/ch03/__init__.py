import trees
import treePlotter
dataSet, labels=trees.createDataSet()

trees.calcShannonEnt(dataSet)

treePlotter.createPlot()