package com.zhongyi.lotusprize.exception;

import com.zhongyi.lotusprize.service.ErrorCode;

@SuppressWarnings("serial")
public class JsonUnmarshallError extends LotusprizeError {

	public JsonUnmarshallError(Throwable ex){
		super(ErrorCode.json_unmarshall,ex);
	}
}
