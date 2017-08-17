library(lattice)
library(ggplot2)
library(caret)
library(AppliedPredictiveModeling)
library(e1071)
library(car)
library(corrplot)

data(segmentationOriginal)

segData <- subset(segmentationOriginal, Case == "Train")
cellID <- segData$Cell
class <- segData$Class
case <- segData$Case

segData <- segData[, -(1:3)]
#找到包括Status的列
statusColNum <- grep("Status", names(segData))
#删除状态列
segData <- segData[, -statusColNum]

# Transformations
#对于预测变量计算偏离度
skewValues <- apply(segData, 2, skewness)
head(skewValues)

#转换数据
Ch1AreaTrans <- BoxCoxTrans(segData$AreaCh1)
Ch1AreaTrans

head(segData)

#散列图
segDatanames=names(segData)
states <- as.data.frame(segData[,c(segDatanames[1:10])])
scatterplotMatrix(states, spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter")



segData[,c("AngleCh1","AvgIntenCh4")]


scatterplotMatrix(~segData$AngleCh1+segData$AvgIntenCh4 , data=segData,
                  spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter Plot Matrix via car Package")


#相关性
correlations <- cor(segData)

dim(correlations)
correlations

corrplot(correlations)

highCorr <- findCorrelation(correlations, cutoff = .90)
length(highCorr)
head(highCorr)

############
filteredSegData <- segData[, ~highCorr]
filtersegDatanames=names(filteredSegData)
filteredSegData <- as.data.frame(filteredSegData[,c(filtersegDatanames[1:10])])

scatterplotMatrix(filteredSegData, spread=FALSE, smoother.args=list(lty=2),
                  main="Main")


#PCA
pcaObject <- prcomp(segData,
                    center = TRUE, scale. = TRUE)

plot(pcaObject)



plot(pcaObject$x[,1], pcaObject$x[,2],
     xlab="pc1",
     ylab="pc2")

percentVariance <- pcaObject$sd^2/sum(pcaObject$sd^2)*100

percentVariance[1:3]

plot(percentVariance,type="b",xlab = "Component",ylab = "Percent Total Variance")


scatterplotMatrix(~pcaObject$x[,c(1:15)], data=pcaObject$x,
                  spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter Plot Matrix via car Package")

pcaObject15<-pcaObject$x[,c(1:15)]
pcaObject15

dotchart(pcaObject15, labels=row.names(pcaObject15), cex=.7,
         main="PCA-15",
         xlab="Component-15")

# dotchart(pcaObject$x, labels=row.names(pcaObject$x), cex=.7,
#          main="PCA",
#          xlab="Component")

head(pcaObject$rotation[1:3])

#####################
trans <- preProcess(segData,
                   method = c("BoxCox", "center", "scale", "pca"))
trans

transformed <- predict(trans, segData)
head(transformed[, 1:5])

# Filtering
nearZeroVar(segData)


# Data Splitting
data(twoClassData)
str(predictors)
str(predictors)
str(classes)


set.seed(1)
trainingRows <- createDataPartition(classes,
                       p = .80, list= FALSE)
head(trainingRows)

trainPredictors <- predictors[trainingRows, ]
trainClasses <- classes[trainingRows]
testPredictors <- predictors[-trainingRows, ]
testClasses <- classes[-trainingRows]
str(trainPredictors)
str(testPredictors)

# Basic Model Building in R
# modelFunction(price ~ numBedrooms + numBaths + acres,
#              data = housingData)
# modelFunction(x = housePredictors, y = price)

trainPredictors <- as.matrix(trainPredictors)
knnFit <- knn3(x = trainPredictors, y = trainClasses, k = 5)
knnFit

testPredictions <- predict(knnFit, newdata = testPredictors,
                   type = "class")

head(testPredictions)
str(testPredictions)

# Determination of Tuning Parameters
library(AppliedPredictiveModeling)
GermanCreditTrain<-data(GermanCredit)
set.seed(1056)

# svmFit <- train(Class ~ .,
#                  data = GermanCreditTrain,
#                 # The "method" argument indicates the model type.
#                  # See ?train for a list of available models.
#                  method = "svmRadial")
svmFit <- train(Class ~ .,data = GermanCredit,method = "svmRadial",
                preProc = c("center", "scale"),tuneLength = 10,
                trControl = trainControl(method = "repeatedcv",repeats = 5))
svmFit

plot(svmFit, scales = list(x = list(log = 2)))

library(car)
# svmvif<-vif(svmFit)


