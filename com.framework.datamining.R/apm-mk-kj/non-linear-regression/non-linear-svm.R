source("non-linear-model.R")
library(kernlab)

#Error code
# svmFit <- ksvm(x = solTrainXtrans, y = solTrainY,
#                kernel ="stringdot", kpar = "automatic",
#                C = 128, epsilon = 0.1)

svmRTuned <- train(solTrainXtrans, solTrainY,
                   method = "svmRadial",
                   preProc = c("center", "scale"),
                   tuneLength = 14,
                   trControl = trainControl(method = "cv"))
svmRTuned
svmRTuned$finalModel
plot(svmRTuned)
