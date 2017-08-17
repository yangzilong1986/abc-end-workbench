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
data(concrete)
str(concrete)
str(mixtures)

# Hmisc包中的describe()函数可返回变量和观测的数量、缺失值和唯一值的数目、平均值、
# 分位数，以及五个最大的值和五个最小的值。
describe(concrete)

#各个预测变量与结构变量的散列点
featurePlot(x = concrete[, -9],
            y = concrete$CompressiveStrength,
            ## Add some space between the panels
            between = list(x = 1, y = 1),
            ## Add a background grid ('g') and a smoother ('smooth')
            type = c("g", "p", "smooth"))

featurePlot(x = concrete[, -9],
            y = concrete$CompressiveStrength,
            ## Add some space between the panels
            between = list(x = 1, y = 1),
            ## Add a background grid ('g') and a smoother ('smooth')
            type = c("g", "p", "smooth"))


#对重复的混合物取平均值并将数据分隔到训练集和测试集
averaged <- ddply(mixtures,
                  .(Cement, BlastFurnaceSlag, FlyAsh, Water,
                    Superplasticizer, CoarseAggregate,
                    FineAggregate, Age),
                  function(x) c(CompressiveStrength =
                                  mean(x$CompressiveStrength)))

set.seed(975)
forTraining <- createDataPartition(averaged$CompressiveStrength,
                                   p = 3/4)[[1]]



trainingSet <- averaged[ forTraining,]
testSet <- averaged[-forTraining,]

trainingData<-trainingSet

modFormulaTxt <- paste("CompressiveStrength ~ (.)^2 + I(Cement^2) + ",
                       " I(BlastFurnaceSlag^2) + I(FlyAsh^2) + I(Water^2) +" ,
                       " I(Superplasticizer^2) + I(CoarseAggregate^2) + " ,
                       " I(FineAggregate^2) + I(Age^2)")

modFormula <- as.formula(modFormulaTxt)


controlObject <- trainControl(method = "repeatedcv", repeats = 5,number = 10)