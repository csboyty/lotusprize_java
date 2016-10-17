package com.zhongyi.lotusprize.domain.account;

import java.io.Serializable;

/**
 * Created by zzy on 14-2-21.
 */
public class AccountProfile implements Serializable {

	private static final long serialVersionUID = -1084134321251006266L;

	private Byte _gender;

	private String _memo;

	private String _organization;

	public AccountProfile() {
	}


	public Byte getGender() {
		return _gender;
	}

	public void setGender(Byte gender) {
		this._gender = gender;
	}

	public String getMemo() {
		return _memo;
	}

	public void setMemo(String memo) {
		this._memo = memo;
	}

	public String getOrganization() {
		return _organization;
	}

	public void setOrganization(String organization) {
		this._organization = organization;
	}

}
