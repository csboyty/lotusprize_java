package com.zhongyi.lotusprize.auth;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.mapper.account.AccountMapper;
import com.zhongyi.lotusprize.mapper.account.InactiveAccountMapper;
import com.zhongyi.lotusprize.service.role.Role;
import com.zhongyi.lotusprize.service.role.Roles;

public class UsernameAndPasswordRealm extends AuthorizingRealm{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String REALM_NAME="u&p_realm";
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private InactiveAccountMapper inactiveAccountMapper;
	
	public UsernameAndPasswordRealm(){
		super();
		HashedCredentialsMatcher cm = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		cm.setHashIterations(10);
		setCredentialsMatcher(cm);
		setName(REALM_NAME);
		setAuthenticationTokenClass(UsernamePasswordToken.class);
	}
	


	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo authzInfo;
        Collection<?> collection = principals.fromRealm(UsernameAndPasswordRealm.REALM_NAME);
        if (collection == null || collection.isEmpty()) {
            authzInfo = null;
        } else {
            Integer accountId = (Integer) (((LotusprizePrincipal)collection.iterator().next()).getAccountId());
            try {
            	authzInfo = new SimpleAuthorizationInfo();
                int roleValue = accountMapper.accountRoleByAccountId(accountId);
                for(Role role:Roles.roles(roleValue)){
                	authzInfo.addRole(role.name());
                }
            } catch (Exception ex) {
                logger.error("doGetAuthorizationInfo() failed accountId:" + accountId, ex);
                authzInfo = null;
            }
        }

        return authzInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		 UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		 String email = token.getUsername();
		 Account account = accountMapper.loadAccountWithSecretByEmail(email);
		 if(account != null){
			  return new SimpleAuthenticationInfo(new LotusprizePrincipal(token.getUsername(), account.getId()),
					  account.getAccountSecret().getPassword(),
					  new LotusprizeByteSource(Hex.decode(account.getAccountSecret().getPasswordSalt())),
					  UsernameAndPasswordRealm.REALM_NAME);
		 }else{
			 int emailCount = inactiveAccountMapper.countByEmail(email);
			 if(emailCount > 0){
	     		throw new AccountInactiveException(email);
	     	 }else{
	     		 return null;
	     	 }
		 }
		  
	}

}
