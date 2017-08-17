source("cubist-model.R")

# testSet$CompressiveStrength<-CompressiveStrength

# set.seed(669)
# nnetModel <- train(CompressiveStrength ~ .,
#                     data = trainingSet,
#                     method = "avNNet",
#                     tuneGrid = nnetGrid,
#                     preProc = c("center", "scale"),
#                     linout = TRUE,
#                     trace = TRUE,
#                     maxit = 10,
#                     trControl = controlObject)


set.seed(669)
earthModel <- train(CompressiveStrength ~ ., data = trainingSet,
                    method = "earth",
                    trace = TRUE,
                    tuneGrid = expand.grid(.degree = 1,
                                           .nprune = 2:25),
                    trControl = controlObject)


# nnetPredictions <- predict(nnetModel, testData)
earthPredictions <- predict(earthModel, testSet)

earthPredictions



plot(earthModel)

#残差分布
#预测值与训练值
xyplot( testSet$CompressiveStrength ~earthPredictions,
       #plot the points (type = 'p') and a background grid ('g')
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Observed")


#resid训练集残差
xyplot(resid(earthModel) ~earthPredictions,
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Residuals")
