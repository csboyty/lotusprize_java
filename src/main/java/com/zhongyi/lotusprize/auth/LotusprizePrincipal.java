package com.zhongyi.lotusprize.auth;

import java.io.Serializable;
import java.util.Objects;


public class LotusprizePrincipal implements Serializable {
	
	private static final long serialVersionUID = -6944252208274637041L;

	private final String uid;
	
	private final Integer accountId;
	
	public LotusprizePrincipal(String uid,Integer accountId){
		super();
		this.uid = uid;
		this.accountId = accountId;
	}
	
	public String getUid(){
		return uid;
	}
	
	public Integer getAccountId(){
		return accountId;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == this)return true;
		if(!(obj instanceof LotusprizePrincipal)){
			return false;
		}
		LotusprizePrincipal other = (LotusprizePrincipal)obj;
		return Objects.equals(this.uid, other.uid) && Objects.equals(this.accountId, other.uid);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(uid,accountId);
	}

}
