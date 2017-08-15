library(ISLR)

names(Hitters)
dim(Hitters)

sum(is.na(Hitters$Salary))
Hitters=na.omit(Hitters)
dim(Hitters)
sum(is.na(Hitters))



# Chapter 6 Lab 2: Ridge Regression and the Lasso

x=model.matrix(Salary~.,Hitters)[,-1]
y=Hitters$Salary

# Ridge Regression

library(glmnet)
grid=10^seq(10,-2,length=100)

#alpha拟合岭回归
ridgeModel=glmnet(x,y,alpha=0,lambda=grid)

coef(ridgeModel)

dim(coef(ridgeModel))

ridgeModel$lambda[50]

coef(ridgeModel)[,50]
#lambda参数与标准信息系数
plot(ridgeModel)

predResult=predict(ridgeModel,s=50,type="coefficients")[1:20,]


set.seed(1)
train=sample(1:nrow(x), nrow(x)/2)
test=(-train)
y.test=y[test]

#基于训练建立岭回归模型并且计算lambda(s)为4时的测试集的MSE
ridge.mod4=glmnet(x[train,],y[train],alpha=0,lambda=grid, thresh=1e-12)

ridge.pred=predict(ridge.mod4,s=4,newx=x[test,])
mean((ridge.pred-y.test)^2)
mean((mean(y[train])-y.test)^2)

ridge.pred=predict(ridge.mod4,s=1e10,newx=x[test,])
mean((ridge.pred-y.test)^2)

ridge.pred=predict(ridge.mod4,s=0,newx=x[test,],exact=T)
mean((ridge.pred-y.test)^2)
lm(y~x, subset=train)

predict(ridge.mod,s=0,exact=T,type="coefficients")[1:20,]

#使用交叉验证来选择调节参数lambda比直接输入
set.seed(1)
cv.out=cv.glmnet(x[train,],y[train],alpha=0)
plot(cv.out)

bestlam=cv.out$lambda.min
bestlam

ridge.pred=predict(ridge.mod,s=bestlam,newx=x[test,])
mean((ridge.pred-y.test)^2)
out=glmnet(x,y,alpha=0)
predict(out,type="coefficients",s=bestlam)[1:20,]




