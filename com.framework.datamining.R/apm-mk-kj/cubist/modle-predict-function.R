
#输入为六个混合物比例组成的向量
#
modelPrediction <- function(x, mod){
  
  if(x[1] < 0 | x[1] > 1) return(10^38)
  if(x[2] < 0 | x[2] > 1) return(10^38)
  if(x[3] < 0 | x[3] > 1) return(10^38)
  if(x[4] < 0 | x[4] > 1) return(10^38)
  if(x[5] < 0 | x[5] > 1) return(10^38)
  if(x[6] < 0 | x[6] > 1) return(10^38)
  #确定水的比例
  x <- c(x, 1 - sum(x))
  if(x[7] < 0.05) return(10^38)
  #将向量转换为数据框、赋给列名并将龄期固定到28天
  tmp <- as.data.frame(t(x))
  
  names(tmp) <- c('Cement','BlastFurnaceSlag','FlyAsh',
                    + 'Superplasticizer','CoarseAggregate',
                    + 'FineAggregate', 'Water')
  tmp$Age <- 28
  
  -predict(mod, tmp)
  
}