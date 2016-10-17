package com.zhongyi.lotusprize.auth;

import org.apache.shiro.authc.AuthenticationException;

@SuppressWarnings("serial")
public class AccountInactiveException extends AuthenticationException {
	
	public AccountInactiveException(){
		
	}
	
	public AccountInactiveException(String message) {
		super(message);
	}

	public AccountInactiveException(Throwable cause) {
		super(cause);
	}

	public AccountInactiveException(String message, Throwable cause) {
		super(message, cause);
	}

}
