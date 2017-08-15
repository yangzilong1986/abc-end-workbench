
#简单线性回归
fit <- lm(weight ~ height, data=women)
summary(fit)

women$weight
fitted(fit)

plot(women$height,women$weight,
     xlab="Height (in inches)",
     ylab="Weight (in pounds)")

abline(fit)

#多项式回归
fit2 <- lm(weight ~ height + I(height^2), data=women)

plot(women$height,women$weight,
     xlab="Height (in inches)",
     ylab="Weight (in lbs)")

lines(women$height,fitted(fit2))



#多元线性回归
states <- as.data.frame(state.x77[,c("Murder", "Population",
                                     "Illiteracy", "Income", "Frost")])

cor(states)

library(car)

scatterplotMatrix(states, spread=FALSE, smoother.args=list(lty=2),
                  main="Scatter Plot Matrix")


fit <- lm(Murder ~ Population + Illiteracy + Income + Frost,
          data=states)
summary(fit)
# 例如本例中，文盲率的回归系数为4.14，表示控制人口、收
# 入和温度不变时，文盲率上升1%，谋杀率将会上升4.14%
# 相反，Frost的系数没有显著不为0（p=0.954），表明当控制其他变量不变时，Frost与Murder
# 不呈线性相关。总体来看，所有的预测变量解释了各州谋杀率57%的方差。
# Coefficients:
#   Estimate Std. Error t value Pr(>|t|)    
# (Intercept) 1.235e+00  3.866e+00   0.319   0.7510    
# Population  2.237e-04  9.052e-05   2.471   0.0173 *  
#   Illiteracy  4.143e+00  8.744e-01   4.738 2.19e-05 ***
#   Income      6.442e-05  6.837e-04   0.094   0.9253    
# Frost       5.813e-04  1.005e-02   0.058   0.9541  


