package com.zhongyi.lotusprize.exception;

import com.zhongyi.lotusprize.service.ErrorCode;

@SuppressWarnings("serial")
public class TimeoutError extends LotusprizeError {
	
	public TimeoutError(){
		super(ErrorCode.timeout);
	}
}
