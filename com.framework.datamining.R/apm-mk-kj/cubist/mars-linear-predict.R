source("cubist-model.R")


set.seed(669)
earthModel <- train(CompressiveStrength ~ ., data = trainingSet,
                    method = "earth",
                    trace = TRUE,
                    tuneGrid = expand.grid(.degree = 1,
                    .nprune = 2:25),
                    trControl = controlObject)

#测试数据集处理
source("modle-predict.R")

source("modle-predict-function.R")

#mars多元自适应线条
marsResults <- startingValues

marsResults$Water <- NA
marsResults$Prediction <- NA

for(i in 1:nrow(marsResults)){
  results <- optim(unlist(marsResults[i,1:6]),
                      modelPrediction,
                      method = "Nelder-Mead",
                      ## Use method = 'SANN' for simulated annealing
                      control=list(maxit=5000),
                       ## The next option is passed to the
                       ## modelPrediction() function
                      mod = earthModel)
  ## Save the predicted compressive strength
  marsResults$Prediction[i] <- -results$value
  ## Also save the final mixture values
  marsResults[i,1:6] <- results$par
}

#计算水的比例  
marsResults$Water <- 1 - apply(marsResults[,1:6], 1, sum)
#保留前三个混合物
marsResults <- marsResults[order(-marsResults$Prediction),][1:3,]
marsResults$Model <- "Mars"
marsResults

pp2 <- preProcess(age28Data[, 1:7], "pca")

pca1 <- predict(pp2, age28Data[, 1:7])
pca1$Data <- "Training Set"
pca1$Data[startPoints] <- "Starting Values"

pca3 <- predict(pp2, marsResults[, names(age28Data[, 1:7])])
pca3$Data <- "Cubist"


pca4 <- predict(pp2, marsResults[, names(age28Data[, 1:7])])
pca4$Data <- "Neural Network"
pcaData <- rbind(pca1, pca3, pca4)

pcaData$Data <- factor(pcaData$Data,
                       levels = c("Training Set","Starting Values",
                       "Cubist","Neural Network"))


lim <- extendrange(pcaData[, 1:2])
xyplot(PC2 ~ PC1, data = pcaData, groups = Data,
       auto.key = list(columns = 2),
       xlim = lim, ylim = lim,
       type = c("g", "p"))





