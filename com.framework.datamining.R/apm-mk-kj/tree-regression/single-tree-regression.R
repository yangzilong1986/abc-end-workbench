source("tree-regression-model.R")
rpartTree <- rpart(solTrainY ~ ., data = trainingData)
rpartTree
plot(rpartTree)

library(partykit)
ptTree<-as.party(rpartTree)
plot(ptTree)

set.seed(100)
rpartTune <- train(solTrainXtrans, solTrainY,
                   method = "rpart2",
                   tuneLength = 10,
                   trControl = trainControl(method = "cv"))

rpartTune

plot(rpartTune)

rpartTune <- train(solTrainXtrans, solTrainY,
                   method = "rpart",
                   tuneLength = 10,
                   trControl = trainControl(method = "cv"))
rpartTune

plot(rpartTune)
