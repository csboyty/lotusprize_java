package com.zhongyi.lotusprize.domain.email;

import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class AccountResetPasswordEmail extends BaseDomain {

	private static final long serialVersionUID = -4595764498333188517L;

	private Integer _accountId;

	private String _password;

	private Integer _emailItemId;

	private String _token;

	public AccountResetPasswordEmail(Integer accountId, Integer emailItemId,
			String password, String token) {
		super();
		this._accountId = accountId;
		this._emailItemId = emailItemId;
		this._password = password;
		this._token = token;
	}

	public AccountResetPasswordEmail() {
		super();
	}

	public Integer getAccountId() {
		return _accountId;
	}

	public void setAccountId(Integer accountId) {
		this._accountId = accountId;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
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

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AccountResetPasswordEmail))
			return false;
		AccountResetPasswordEmail other = (AccountResetPasswordEmail) o;
		return Objects.equals(_accountId, other._accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_accountId);
	}

}
