source("cubist-model.R")

set.seed(669)
linearReg <- train(modFormula,
                data = trainingSet,
                method = "lm", intercept=TRUE,
                trControl = controlObject)
linearReg
#上面共使用了8个变量，即没有使用扩展的模型

modForm<-modFormula
set.seed(669)
plsModel <- train(modForm, data = trainingSet,
                     method = "pls",
                     preProc = c("center","scale"),
                     tuneLength = 5,
                     trControl = controlObject)
enetGrid <- expand.grid(.lambda = c(0, .001, .01, .1), .fraction = seq(0.05, 1, length = 20))



set.seed(669)
enetModel <- train(modForm, data = trainingSet,
                   method = "enet",
                   preProc = c("center", "scale"),
                   tuneGrid = enetGrid,
                   trControl = controlObject)

set.seed(669)
earthModel <- train(CompressiveStrength ~ ., data = trainingSet,
                    method = "earth",
                     tuneGrid = expand.grid(.degree = 1,
                     .nprune = 2:25),
                     trControl = controlObject)


set.seed(669)
svmRModel <- train(CompressiveStrength ~ ., data = trainingSet,
                   method = "svmRadial",
                   tuneLength = 5,
                   preProc = c("center", "scale"),
                   trControl = controlObject)

nnetGrid <- expand.grid(.decay = c(0.001, .01, .1),
                         .size = seq(1, 27, by = 2),
                         .bag = FALSE)
# 
# set.seed(669)
# nnetModel <- train(CompressiveStrength ~ .,
#                     data = trainingSet,
#                     method = "avNNet",
#                     tuneGrid = nnetGrid,
#                     preProc = c("center", "scale"),
#                     linout = TRUE,
#                     trace = FALSE,
#                     maxit = 10,
#                     trControl = controlObject)

library(rpart)
set.seed(669)
rpartModel <- train(CompressiveStrength ~ .,
                     data = trainingSet,
                     method = "rpart",
                     tuneLength = 30,
                     trControl = controlObject)


set.seed(669)
ctreeModel <- train(CompressiveStrength ~ .,
                     data = trainingSet,
                     method = "ctree",
                     tuneLength = 10,
                     trControl = controlObject)
# set.seed(669)
# mtModel <- train(CompressiveStrength ~ .,
#                   data = trainingSet,
#                   method = "M5",
#                   trControl = controlObject)


set.seed(669)
treebagModel <- train(CompressiveStrength ~ .,
                       data = trainingSet,
                       method = "treebag",
                       trControl = controlObject)

# set.seed(669)
# rfModel <- train(CompressiveStrength ~ .,
#                   data = trainingSet,
#                   method = "rf",
#                   tuneLength = 10,
#                   ntrees = 100,
#                   importance = TRUE,
#                   trControl = controlObject)

gbmGrid <- expand.grid(.interaction.depth = seq(1, 7, by = 2),
                       .n.trees = seq(100, 1000, by = 50),
                       .shrinkage = c(0.01, 0.1))

set.seed(669)
gbmModel <- train(CompressiveStrength ~ .,
                   data = trainingSet,
                   method = "gbm",
                   tuneGrid = gbmGrid,
                   verbose = FALSE,
                   trControl = controlObject)


# cubistGrid <- expand.grid(.committees = c(1, 5, 10, 50, 75, 100),
#                             .neighbors = c(0, 1, 3, 5, 7, 9))

# 
# set.seed(669)
# cbModel <- train(CompressiveStrength ~ .,data = trainingSet,
#                  method = "cubist",tuneGrid = cubistGrid,
#                  trControl = controlObject)

allResamples <- resamples(list("Linear Reg" = linearReg,
                                "PLS" = plsModel,
                                "Elastic Net" = enetModel,
                                MARS = earthModel,
                                SVM = svmRModel,
                                # "Neural Networks" = nnetModel,
                                CART = rpartModel,
                                "Cond Inf Tree" = ctreeModel,
                                "Bagged Tree" = treebagModel
                                # "Boosted Tree" = gbmModel,
                                # "Random Forest" = rfModel,
                                # Cubist = cbModel
                               ))
par(mfrow=c(2, 2))
plot(linearReg$finalModel)
plot(plsModel)
plot(enetModel)
plot(earthModel)
plot(svmRModel)
plot(rpartModel)
plot(ctreeModel)


parallelplot(allResamples)
parallelplot(allResamples, metric = "Rsquared")

dotplot(allResamples, 
        scales =list(x = list(relation = "free")), 
        between = list(x = 2))

bwplot(allResamples,
       metric = "RMSE")

densityplot(allResamples,
            auto.key = list(columns = 3),
            pch = "|")

xyplot(allResamples,
       models = c("Linear Reg", "PLS","Elastic Net"),
       metric = "RMSE")

splom(allResamples, metric = "RMSE")
splom(allResamples, variables = "metrics")
