#The R packages elasticnet, caret, lars, MASS, pls and stats
library(lattice)
library(ggplot2)
library(caret)
library(desirability)
library(survival)
library(Formula)
library(Hmisc)
library(plyr)
library(AppliedPredictiveModeling)

library(nnet)
library(pls)
library(kernlab)
library(elasticnet)
library(lars)
library(earth)
library(plotmo)
#溶解度数据获取
library(AppliedPredictiveModeling)
data(solubility)
##以solT开头的数据对象
ls(pattern = "^solT")
set.seed(2)

#每种化合物的溶解度都包含在数值型向量solTrainY和solTestX
#训练集预测变量和结果变量包含在同一个框架中
trainingData <- solTrainXtrans
trainingData$Solubility <- solTrainY
#强相关量处理
tooHigh <- findCorrelation(cor(solTrainXtrans), cutoff = .75)
trainXnnet <- solTrainXtrans[, -tooHigh]
testXnnet <- solTestXtrans[, -tooHigh]

#用于交叉验证
ctrl <- trainControl(method = "cv", number = 10)