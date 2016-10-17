package com.zhongyi.lotusprize.domain.account;

import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class InactiveAccount extends BaseDomain{
	
	private static final long serialVersionUID = -349086776673634092L;

	private Integer _id;
	
	private String _fullname;
	
	private String _email;
	
	private String _mobile;
	
	private String _address;
	
	private String _organization;
	
	private String _password;
	
	private Date _createTime;
	

	public InactiveAccount() {
		super();
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public String getFullname() {
		return _fullname;
	}

	public void setFullname(String fullname) {
		this._fullname = fullname;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String email) {
		this._email = email;
	}

	public String getMobile() {
		return _mobile;
	}

	public void setMobile(String mobile) {
		this._mobile = mobile;
	}

	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		this._address = address;
	}
	
	public String getOrganization(){
		return _organization;
	}
	
	public void setOrganization(String organization){
		this._organization=organization;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	public Date getCreateTime() {
		return _createTime;
	}

	public void setCreateTime(Date createTime) {
		this._createTime = createTime;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this)return true;
		if(!(o instanceof InactiveAccount))return false;
		InactiveAccount other = (InactiveAccount)o;
		return Objects.equals(_id, other._id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id);
	}
	
	
	

}
