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

#每种化合物的溶解度都包含在数值型向量solTrainY和solTestX
#训练集预测变量和结果变量包含在同一个框架中
trainingData <- solTrainXtrans
trainingData$Solubility <- solTrainY

summary(trainingData)

#分析原始数据
analysisRawData<-function(){
  names(trainingData)
  sample(names(solTrainX), 18)
  states <- as.data.frame(trainingData[,sample(names(solTrainX), 10)])
  scatterplotMatrix(states, main="ScatterPackage")
}
#analysisRawData()
#用于交叉验证
ctrl <- trainControl(method = "cv", number = 10)

#数据分析相关性分析
correlations <- cor(trainingData)
dim(correlations)
corrplot(correlations)
correlations[1:4, 1:4]
#大于.75的相关性
highCorr <- findCorrelation(correlations, cutoff = .75)
length(highCorr)

#回归诊断标准方法
#所有预测变量放到一起
lmFitAllPredictors <- lm(Solubility ~ ., data = trainingData)
summary(lmFitAllPredictors)

#残差分布
plot(lmFitAllPredictors)

xyplot(solTrainY ~ predict(lmFitAllPredictors),
       #plot the points (type = 'p') and a background grid ('g')
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Observed")

#残差分布
#resid训练集残差
xyplot(resid(lmFit1) ~ predict(lmFitAllPredictors),
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Residuals")

par(mfrow=c(2,2))
plot(lmFitAllPredictors)
states <- as.data.frame(trainingData[,sample(names(solTrainX))])
qqPlot(lmFitAllPredictors, labels=row.names(states), id.method="identify",
       simulate=TRUE, main="Q-Q Plot")

residplot <- function(fit, nbreaks=10) {
  z <- rstudent(fit)
  hist(z, breaks=nbreaks, freq=FALSE,
       xlab="Studentized Residual",
       main="Distribution of Errors")
  rug(jitter(z), col="brown")
  curve(dnorm(x, mean=mean(z), sd=sd(z)),
        add=TRUE, col="blue", lwd=2)
  lines(density(z)$x, density(z)$y,
        col="red", lwd=2, lty=2)
  legend("topright",
         legend = c( "Normal Curve", "Kernel Density Curve"),
         lty=1:2, col=c("blue","red"), cex=.7)
}

#线性
#通过成分残差图（component plus residual plot）也称偏残差图（partial residual plot）
crPlots(lmFitAllPredictors)

#检验同方差性
ncvTest(lmFitAllPredictors)
spreadLevelPlot(lmFitAllPredictors)

#检测多重共线性
vif(lmFitAllPredictors)

#离群点是指那些模型预测效果不佳的观测点
outlierTest(lmFitAllPredictors)

#高杠杆值观测点，即与其他预测变量有关的离群点。
#换句话说，它们是由许多异常的预测变量值组合起来的，与响应变量值没有关系。
hat.plot <- function(fit) {
  p <- length(coefficients(fit))
  n <- length(fitted(fit))
  plot(hatvalues(fit), main="Index Plot of Hat Values")
  abline(h=c(2,3)*p/n, col="red", lty=2)
  identify(1:n, hatvalues(fit), names(hatvalues(fit)))
}
hat.plot(lmFitAllPredictors)

cutoff <- 4/(nrow(states)-length(lmFitAllPredictors$coefficients)-2)
plot(lmFitAllPredictors, which=4, cook.levels=cutoff)
abline(h=cutoff, lty=2, col="red")

influencePlot(lmFitAllPredictors, id.method="identify", main="Influence Plot",
              sub="Circle size is proportional to Cook's distance")

avPlots(lmFitAllPredictors, ask=FALSE, id.method="identify")

lmPred1 <- predict(lmFitAllPredictors, solTestXtrans)
head(lmPred1)
#将观察值和预测值放到一个数据框中
lmValues1 <- data.frame(obs = solTestY, pred = lmPred1)
defaultSummary(lmValues1)
