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

##为了对罚项进行调优，可以使用trian方法
#定义备选值
ridgeGrid <- data.frame(.lambda = seq(0, .1, length = 15))

set.seed(100)
ridgeRegFit <- train(solTrainXtrans, solTrainY,
                     method = "ridge",
                     ## Fir the model over many penalty values
                     tuneGrid = ridgeGrid,
                     trControl = ctrl,
                     ## put the predictors on the same scale
                     preProc = c("center", "scale"))


plot(ridgeRegFit$results$lambda,ridgeRegFit$results$RMSE,
     xlab = "lambda",ylab = "RMSE(Cross-Validation)",
     main = "ridge", sub="NI-PALS",
     #xlim=c(0, 20), ylim=c(0, 2),
     col="blue",type="b",lty=1)
legend("topright", inset=.05, c("ridge"), lty=c(1),col=c("blue"))


plot(ridgeRegFit)
