source("non-linear-model.R")

marsFit <- earth(solTrainXtrans, solTrainY)
summary(marsFit)
plot(marsFit)
plotmo(marsFit)

#使用外部抽样方法对模型调优
marsGrid <- expand.grid(.degree = 1:2, .nprune = 2:38)
set.seed(100)
marsTuned <- train(solTrainXtrans, solTrainY,
                    method = "earth",
                    # Explicitly declare the candidate models to test
                    tuneGrid = marsGrid,
                    trControl = trainControl(method = "cv"))


head(predict(marsTuned, solTestXtrans))

xyplot(solTrainY ~ predict(marsTuned),
       #plot the points (type = 'p') and a background grid ('g')
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Observed")

varImp(marsTuned)
