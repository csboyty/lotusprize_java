package com.zhongyi.lotusprize.exception;

import com.zhongyi.lotusprize.service.ErrorCode;

@SuppressWarnings("serial")
public class JsonMarshallError extends LotusprizeError {
	
	public JsonMarshallError(Throwable e){
		super(ErrorCode.json_marshall,e);
	}

}
