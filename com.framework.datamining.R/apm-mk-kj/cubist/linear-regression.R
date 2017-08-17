source("cubist-model.R")

set.seed(669)
linearReg <- train(modFormula,
                   data = trainingSet,
                   method = "lm", intercept=TRUE,
                   trControl = controlObject)
linearReg

#残差分布
plot(linearReg)

xyplot(testSet ~ predict(linearReg),
       #plot the points (type = 'p') and a background grid ('g')
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Observed")

#残差分布
#resid训练集残差
xyplot(resid(testSet) ~ predict(linearReg),
       type = c("p", "g"),
       xlab = "Predicted", ylab = "Residuals")
