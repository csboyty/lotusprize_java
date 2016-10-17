package com.zhongyi.lotusprize.auth;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.AbstractAuthenticationStrategy;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.mapper.account.InactiveAccountMapper;

public class LotusprizeAuthenticationStrategy extends AbstractAuthenticationStrategy{
	
	public LotusprizeAuthenticationStrategy(){
		super();
	}
	
	@Autowired
	protected InactiveAccountMapper inactiveAccountMapper;
	
	public AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
		 if (aggregate == null || CollectionUtils.isEmpty(aggregate.getPrincipals())) {
			 if(token instanceof UsernamePasswordToken){
				 String email = ((UsernamePasswordToken)token).getUsername();
				 int emailCount = inactiveAccountMapper.countByEmail(email);
				 if(emailCount > 0){
		     		throw new AccountInactiveException(email);
		     	 }
			 }
			 throw new AuthenticationException("Authentication token of type [" + token.getClass() + "] " +
	                 "could not be authenticated by any configured realms.  Please ensure that at least one realm can " +
	                 "authenticate these tokens.");
		 }
		 return aggregate;
	}

}

