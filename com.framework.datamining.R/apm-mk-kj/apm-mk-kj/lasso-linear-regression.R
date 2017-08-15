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



enetModel <- enet(x = as.matrix(solTrainXtrans), y = solTrainY,
                  lambda = 0.01, normalize = TRUE)

enetPred <- predict(enetModel, newx = as.matrix(solTestXtrans),
                    s = .1, mode = "fraction",
                    type = "fit")

names(enetPred)
head(enetPred$fit)
enetCoef<- predict(enetModel, newx = as.matrix(solTestXtrans),
                   s = .1, mode = "fraction",
                   type = "coefficients")

tail(enetCoef$coefficients)
enetGrid <- expand.grid(.lambda = c(0, 0.01, .1),
                        .fraction = seq(.05, 1, length = 20))
set.seed(100)

enetTune <- train(solTrainXtrans, solTrainY,
                  method = "enet",
                  tuneGrid = enetGrid,
                  trControl = ctrl,
                  preProc = c("center", "scale"))

plot(enetTune)



