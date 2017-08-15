#The R packages elasticnet, caret, lars, MASS, pls and stats
#需要的包
library(elasticnet)
library(caret)
library(lars)
library(MASS) 
library(pls)
library(stats)

#溶解度数据获取
library(AppliedPredictiveModeling)
data(solubility)
##以solT开头的数据对象
ls(pattern = "^solT")
set.seed(2)
sample(names(solTrainX), 8)

#用于交叉验证
ctrl <- trainControl(method = "cv", number = 10)

#Ordinary Linear Regression
#lm:trainingData <- solTrainXtrans
#Add the solubility outcome
trainingData <- solTrainXtrans
trainingData$Solubility <- solTrainY
head(trainingData)

#Penalized Regression Models
#岭回归可以使用lm.ridge或者elasticnet中的enet，使用enet时，lambda指定岭回归的罚参数
ridgeModel <- enet(x = as.matrix(solTrainXtrans),
                   y = solTrainY,
                   lambda = 0.001)


plot(ridgeModel)

#预测
ridgePred <- predict(ridgeModel, newx = as.matrix(solTestXtrans),
                     s = 1, mode = "fraction",type = "fit")

# ridgeVif=vif(ridgePred)

