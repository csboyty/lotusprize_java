package com.zhongyi.lotusprize.exception;

import com.zhongyi.lotusprize.service.ErrorCode;

@SuppressWarnings("serial")
public class LotusprizeError extends RuntimeException {
	
	private final ErrorCode _errorCode;
	
	public LotusprizeError(ErrorCode errorCode){
		super();
		this._errorCode = errorCode; 
	}
	
	public LotusprizeError(ErrorCode errorCode,String message){
		this(errorCode,message,null);
	}
	
	public LotusprizeError(ErrorCode errorCode,Throwable ex){
		this(errorCode,null,ex);
	}
	
	public LotusprizeError(ErrorCode errorCode,String message,Throwable ex){
		super(message,ex);
		this._errorCode = errorCode;
	}
	
	public ErrorCode errorCode(){
		return _errorCode;
	}

}
