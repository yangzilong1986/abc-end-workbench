package com.abc.framework.core.web.domain;

public class ErrorInfo implements java.io.Serializable{

	private static final long serialVersionUID = -5855670756989555842L;
	private Object errorCode;
	private String errorInfo;
	
	public ErrorInfo(){
		
	}
	public ErrorInfo(Object errorCode,String errorInfo){
		this.errorCode = errorCode;
		this.errorInfo = errorInfo;
	}
	public Object getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Object errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	
	@Override
	public String toString() {
		return "ErrorInfo [errorCode=" + errorCode + ", errorInfo=" + errorInfo + "]";
	}


}
