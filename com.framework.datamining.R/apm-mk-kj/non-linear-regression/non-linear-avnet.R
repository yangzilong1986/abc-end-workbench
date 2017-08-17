source("non-linear-model.R")

#创建需要进行模型评估的备选集合
nnetGrid <- expand.grid(.decay = c(0, 0.01, .1),
                        .size = c(1:10),
                         ## The next option is to use bagging (see the
                         ## next chapter) instead of different random
                         ## seeds.
                         .bag = FALSE)
set.seed(100)
nnetTune <- train(solTrainXtrans, solTrainY,
                   method = "avNNet",
                   tuneGrid = nnetGrid,
                   trControl = ctrl,
                   ## Automatically standardize data prior to modeling
                   ## and prediction
                   preProc = c("center", "scale"),
                   linout = TRUE,
                   trace = TRUE,
                   MaxNWts = 10 * (ncol(trainXnnet) + 1) + 10 + 1,
                   maxit = 5)

summary(nnetTune$results)

plot(nnetTune)

