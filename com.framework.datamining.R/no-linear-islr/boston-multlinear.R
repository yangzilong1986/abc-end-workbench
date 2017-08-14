# Title     : TODO
# Objective : TODO
# Created by: admin
# Created on: 2017/8/13
set.seed(1)
library(MASS)
attach(Boston)

lm.fit = lm(nox ~ poly(dis, 3), data = Boston)
summary(lm.fit)
