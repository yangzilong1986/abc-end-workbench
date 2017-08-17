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


set.seed(100)
pcr.fit=pcr(Solubility~., data=trainingData,scale=TRUE,validation="CV")
summary(pcr.fit)
validationplot(pcr.fit,val.type="MSEP")

set.seed(100)
pls.fit=plsr(Solubility~., data=trainingData,scale=TRUE, validation="CV")

validationplot(pls.fit,val.type="MSEP")

summary(pls.fit)
