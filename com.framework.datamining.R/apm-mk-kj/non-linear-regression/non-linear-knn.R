source("non-linear-model.R")
knnDescr <- solTrainXtrans[, -nearZeroVar(solTrainXtrans)]

summary(knnDescr)

set.seed(100)