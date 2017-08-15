
#(a) 使用rnorm ()函数生成长度为n =100 的预测变量X和长度n =100的噪声向量
set.seed(1)
X = rnorm(100)
eps = rnorm(100)


#(b) 依据以下模现产生长度为n =100 的响应变量Y

beta0 = 3
beta1 = 2
beta2 = -3
beta3 = 0.3
Y = beta0 + beta1 * X + beta2 * X^2 + beta3 * X^3 + eps

library(leaps)
data.full = data.frame(y = Y, x = X)
mod.full = regsubsets(y ~ poly(x, 10, raw = T), data = data.full, nvmax = 10)
mod.summary = summary(mod.full)

# Find the model size for best cp, BIC and adjr2
which.min(mod.summary$cp)
which.min(mod.summary$bic)
which.max(mod.summary$adjr2)

# Plot cp, BIC and adjr2
plot(mod.summary$cp, xlab = "Subset Size", ylab = "Cp", pch = 20, type = "l")
points(3, mod.summary$cp[3], pch = 4, col = "red", lwd = 7)

plot(mod.summary$bic, xlab = "Subset Size", ylab = "BIC", pch = 20, type = "l")
points(3, mod.summary$bic[3], pch = 4, col = "red", lwd = 7)

plot(mod.summary$adjr2, xlab = "Subset Size", ylab = "Adjusted R2", pch = 20, 
     type = "l")
points(3, mod.summary$adjr2[3], pch = 4, col = "red", lwd = 7)

coefficients(mod.full, id = 3)
