library(caret)
library(AppliedPredictiveModeling)
data(segmentationOriginal)

segData <- subset(segmentationOriginal, Case == "Train")
cellID <- segData$Cell
class <- segData$Class
case <- segData$Case
segData <- segData[, -(1:3)]
statusColNum <- grep("Status", names(segData))
statusColNum
segData <- segData[, -statusColNum]

# Transformations
library(e1071)
skewness(segData$AngleCh1)
skewValues <- apply(segData, 2, skewness)
head(skewValues)

Ch1AreaTrans <- BoxCoxTrans(segData$AreaCh1)
Ch1AreaTrans
head(segData$AreaCh1)

predict(Ch1AreaTrans, head(segData$AreaCh1))

pcaObject <- prcomp(segData,
                    center = TRUE, scale. = TRUE)

library(car)
scatterplotMatrix(~segData$AngleCh1+segData$AvgIntenCh4 , data=segData,
                  spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter Plot Matrix via car Package")


plot(pcaObject)



plot(pcaObject$x[,1], pcaObject$x[,2],
     xlab="pc1",
     ylab="pc2")

percentVariance <- pcaObject$sd^2/sum(pcaObject$sd^2)*100

percentVariance[1:3]
head(pcaObject$x[, 1:5])

plot(percentVariance,type="b",xlab = "Component",ylab = "Percent Total Variance")


scatterplotMatrix(~pcaObject$x[,c(1:5)], data=pcaObject$x,
                  spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter Plot Matrix via car Package")

plot(pcaObject$x)


head(pcaObject$rotation[1:3])

trans <- preProcess(segData,
                   method = c("BoxCox", "center", "scale", "pca"))
trans

transformed <- predict(trans, segData)
head(transformed[, 1:5])

# Filtering
nearZeroVar(segData)

correlations <- cor(segData)

dim(correlations)
correlations[1:4, 1:4]
library(corrplot)
corrplot(correlations, order = "hclust")

highCorr <- findCorrelation(correlations, cutoff = .75)
length(highCorr)
head(highCorr)
filteredSegData <- segData[, -highCorr]


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


