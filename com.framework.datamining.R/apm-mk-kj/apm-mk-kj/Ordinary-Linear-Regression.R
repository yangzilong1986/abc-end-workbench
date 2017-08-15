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
lmFitAllPredictors <- lm(Solubility ~ ., data = trainingData)
summary(lmFitAllPredictors)
lmPred1 <- predict(lmFitAllPredictors, solTestXtrans)
head(lmPred1)
#将观察值和预测值放到一个数据框中
lmValues1 <- data.frame(obs = solTestY, pred = lmPred1)
defaultSummary(lmValues1)

#稳健线性回归模型rlm
#10a折叉验证
rlmFitAllPredictors <- rlm(Solubility ~ ., data = trainingData)

set.seed(100)
lmFit1 <- train(x = solTrainXtrans, y = solTrainY,
                method = "lm", trControl = ctrl)
lmFit1

xyplot(solTrainY ~ predict(lmFit1),
        #plot the points (type = 'p') and a background grid ('g')
        type = c("p", "g"),
        xlab = "Predicted", ylab = "Observed")

#残差分布
#resid训练集残差
xyplot(resid(lmFit1) ~ predict(lmFit1),
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Residuals")


#移除强相关的预测变量

corThresh <- .9
tooHigh <- findCorrelation(cor(solTrainXtrans), corThresh)
corrPred <- names(solTrainXtrans)[tooHigh]
trainXfiltered <- solTrainXtrans[, -tooHigh]

set.seed(100)
lmFiltered <- train(solTrainXtrans, solTrainY, method = "lm",trControl = ctrl)
lmFiltered

#稳健线性回归模型rlm使用train函数
set.seed(100)
rlmPCA <- train(solTrainXtrans, solTrainY,method = "rlm",preProcess = "pca",trControl = ctrl)
rlmPCA

#偏最小二乘PLS、PCR（主成分回归）
#实现包pls
plsFit <- plsr(Solubility ~ ., data = trainingData)
predict(plsFit, solTestXtrans[1:5,], ncomp = 1:2)

set.seed(100)
plsTune <- train(solTrainXtrans, solTrainY,
                  method = "pls",
                  ## The default tuning grid evaluates
                  ## components 1... tuneLength
                  tuneLength = 20,
                  trControl = ctrl,
                  preProc = c("center", "scale"))
plsTune
plot(plsTune$results$ncomp,plsTune$results$RMSE,
     xlab = "component",ylab = "RMSE(Cross-Validation)",
     main = "PLS", sub="NI-PALS",
     #xlim=c(0, 20), ylim=c(0, 2),
     col="red",type="b",lty=1)
legend("topright", inset=.05, c("pls"), lty=c(1),col=c("red"))

#Penalized Regression Models
#岭回归可以使用lm.ridge或者elasticnet中的enet，使用enet时，lambda指定岭回归的罚参数
ridgeModel <- enet(x = as.matrix(solTrainXtrans),
                   y = solTrainY,
                  lambda = 0.001)

#预测
ridgePred <- predict(ridgeModel, newx = as.matrix(solTestXtrans),
                  s = 1, mode = "fraction",type = "fit")
head(ridgePred$fit)




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

ridgeRegFit

plot(ridgeRegFit)
