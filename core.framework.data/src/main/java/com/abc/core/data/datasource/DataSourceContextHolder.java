package com.abc.core.data.datasource;

public class DataSourceContextHolder {  
	public final static String DATA_SOURCE_MYSQL="dataSourceOne";
	public final static String DATA_SOURCE_SAVLE="dataSourceTwo";
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();  
    /** 
     * @Description: 设置数据源类型 
     * @param dataSourceType  数据库类型 
     * @return void 
     * @throws 
     */  
    public static void setDataSourceType(String dataSourceType) {  
        contextHolder.set(dataSourceType);  
    }  
      
    /** 
     * @Description: 获取数据源类型 
     * @param  
     * @return String 
     * @throws 
     */  
    public static String getDataSourceType() {  
    	String key=contextHolder.get();
    	if(key!=null){
    		System.err.println("DataSourceContextHolder getDataSourceType is "+key);
    	}
        return contextHolder.get();  
    }  
      
    /** 
     * @Description: 清除数据源类型 
     * @param  
     * @return void 
     * @throws 
     */  
    public static void clearDataSourceType() {  
        contextHolder.remove();  
    }  
} 