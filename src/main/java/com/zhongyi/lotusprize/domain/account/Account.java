package com.zhongyi.lotusprize.domain.account;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhongyi.lotusprize.domain.BaseDomain;

/**
 * Created by zzy on 14-2-21.
 */
public class Account extends BaseDomain{

	private static final long serialVersionUID = 2532703440438093442L;

	public enum Status{
        active,disable
    }

    private Integer _id;

    private String _fullname;

    private String _email;

    private String _mobile;
    
    private String _address;

    private Status _status;

    private AccountSecret _accountSecret;

    private AccountProfile _accountProfile;

    private Integer _roleValue;
    

    public Account() {
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

    public Status getStatus() {
        return _status;
    }

    public void setStatus(Status status) {
        this._status = status;
    }
    
    @JsonIgnore
    public AccountSecret getAccountSecret() {
        return _accountSecret;
    }

    public void setAccountSecret(AccountSecret accountSecret) {
        this._accountSecret = accountSecret;
    }

    public AccountProfile getAccountProfile() {
        return _accountProfile;
    }

    public void setAccountProfile(AccountProfile accountProfile) {
        this._accountProfile = accountProfile;
    }

    public String getAddress(){
    	return _address;
    }
    
    public void setAddress(String address){
    	this._address = address;
    }

    public Integer getRoleValue() {
        return _roleValue;
    }

    public void setRoleValue(Integer roleValue) {
        this._roleValue = roleValue;
    }

	public boolean equals(Object o){
        if(o == this)return true;
        if(!(o instanceof Account))
            return false;
        Account other = (Account)o;
        return Objects.equals(_email,other._email);
    }

    public int hashCode(){
        return Objects.hash(_email);
    }

}
