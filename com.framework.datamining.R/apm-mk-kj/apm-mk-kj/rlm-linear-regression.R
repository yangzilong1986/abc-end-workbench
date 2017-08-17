#The R packages elasticnet, caret, lars, MASS, pls and stats
#需要的包
library(elasticnet)
library(caret)
library(lars)
library(MASS) 
library(pls)
library(stats)
library(car)
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

#稳健线性回归模型rlm
#10a折叉验证
set.seed(100)
lmFit <- train(x = solTrainXtrans, y = solTrainY,
                method = "lm", trControl = ctrl)

lmFit

xyplot(solTrainY ~ predict(lmFit),
        ## plot the points (type = 'p') and a background grid ('g')
        type = c("p", "g"),
        xlab = "Predicted", ylab = "Observed")
xyplot(resid(lmFit) ~ predict(lmFit),
         type = c("p", "g"),
         xlab = "Predicted", ylab = "Residuals")

corThresh <- .9
tooHigh <- findCorrelation(cor(solTrainXtrans), corThresh)
corrPred <- names(solTrainXtrans)[tooHigh]
trainXfiltered <- solTrainXtrans[, -tooHigh]
testXfiltered <- solTestXtrans[, -tooHigh]

set.seed(100)
lmFiltered <- train(trainXfiltered, solTrainY, method = "lm",
                    trControl = ctrl)
lmFiltered

set.seed(100)
rlmPCA <- train(solTrainXtrans, solTrainY,
                method = "rlm",
                preProcess = "pca",
                trControl = ctrl)

rlmPCA









