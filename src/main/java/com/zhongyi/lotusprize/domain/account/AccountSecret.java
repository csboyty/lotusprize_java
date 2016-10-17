package com.zhongyi.lotusprize.domain.account;

import java.io.Serializable;

/**
 * Created by zzy on 14-2-21.
 */
public class AccountSecret implements Serializable {

	private static final long serialVersionUID = -5027993988964742380L;

	private String _password;

	private String _passwordSalt;

	public AccountSecret() {

	}
	

	public AccountSecret(String password, String passwordSalt) {
		super();
		this._password = password;
		this._passwordSalt = passwordSalt;
	}


	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	public String getPasswordSalt() {
		return _passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this._passwordSalt = passwordSalt;
	}

}
