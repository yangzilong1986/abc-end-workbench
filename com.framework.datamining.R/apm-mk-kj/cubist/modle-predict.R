#source("cubist-model.R")


#预测方法
# source("mars-linear-predict.R")

#配方的距离作为一种不相似性的度量，所以需要先对数据进行预测处理
#以保证每个测试变量拥有相同的均差和方差
age28Data <- subset(trainingData, Age == 28)
#移除两个变量龄Age和抗压强度
pp1 <- preProcess(age28Data[, -(8:9)], c("center", "scale"))

scaledTrain <- predict(pp1, age28Data[, 1:7])

set.seed(91)
startMixture <- sample(1:nrow(age28Data), 1)
starters <- scaledTrain[startMixture, 1:7]

pool <- scaledTrain

index <- maxDissim(starters, pool, 14)
startPoints <- c(startMixture, index)

starters <- age28Data[startPoints,1:7]

startingValues <- starters[, -4]
startingValues
