package com.zhongyi.lotusprize.domain.email;

import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class AccountActiveEmail extends BaseDomain {
	
	private static final long serialVersionUID = 2833587156305614647L;

	private Integer _inactiveAccountId;
	
	private Integer _emailItemId;
	
	private String _token;

	public AccountActiveEmail(Integer inactiveAccountId, Integer emailItemId,String token) {
		super();
		this._inactiveAccountId = inactiveAccountId;
		this._emailItemId = emailItemId;
		this._token = token;
	}

	public AccountActiveEmail() {
		super();
	}

	public Integer getInactiveAccountId() {
		return _inactiveAccountId;
	}

	public void setInactiveAccountId(Integer inactiveAccountId) {
		this._inactiveAccountId = inactiveAccountId;
	}

	public Integer getEmailItemId() {
		return _emailItemId;
	}

	public void setEmailItemId(Integer emailItemId) {
		this._emailItemId = emailItemId;
	}

	public String getToken() {
		return _token;
	}

	public void setToken(String token) {
		this._token = token;
	}

	public boolean equals(Object o){
		if(o == this)return true;
		if(!(o instanceof AccountActiveEmail))return false;
		AccountActiveEmail other = (AccountActiveEmail)o;
		return Objects.equals(_inactiveAccountId, other._inactiveAccountId);
	}
	
	public int hashCode(){
		return Objects.hash(_inactiveAccountId);
	}
	
	
	
	
	
	

}
