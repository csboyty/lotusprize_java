package com.zhongyi.lotusprize.service.account;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.zhongyi.lotusprize.auth.PasswordSaltGenerator;
import com.zhongyi.lotusprize.auth.PasswordSaltGenerator.PasswordSalt;
import com.zhongyi.lotusprize.auth.cache.ShiroRedisAuthenticationCache;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.account.AccountProfile;
import com.zhongyi.lotusprize.domain.account.AccountSecret;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.SystemEventBus;
import com.zhongyi.lotusprize.service.role.Role;

@Component
public class AccountOperationHandler extends BaseAccountHandler {
	
	
	public void createOrUpdateAccount(final Integer accountId,final String fullname,final String email,
			final String mobile,final String address,
			String memo,String organization,Role role){
		Account cachedAccount  = null;
		if(accountId != null){
			cachedAccount = accountRedis.accountById(accountId);
		}
		final Account account = cachedAccount == null ? new Account():cachedAccount;
		account.setEmail(email);
		account.setFullname(fullname);
		account.setMobile(mobile);
		account.setAddress(address);
		if(role != null)
			account.setRoleValue(role.value());
		account.setStatus(Account.Status.active);
		if(accountId == null){
			String password = email.substring(0,email.lastIndexOf("."));
			PasswordSalt passwordSalt = PasswordSaltGenerator.instance().generate();
			final AccountSecret accountSecret = new AccountSecret();
			accountSecret.setPassword(passwordSalt.passwordHashed(password));
			accountSecret.setPasswordSalt(passwordSalt.saltHex());
			account.setAccountSecret(accountSecret);
		}
		final AccountProfile accountProfile = new AccountProfile();
		
		accountProfile.setGender((byte)1);
		accountProfile.setMemo(memo);
		accountProfile.setOrganization(organization);
		account.setAccountProfile(accountProfile);
		try{
			txRunner.doInTransaction(new ITransactionOperation(){
				@Override
				public Void run() {
					Integer _accountId;
					if(accountId == null){
						accountMapper.insertAccount(account);
						accountMapper.insertOrUpdateAccountSecret(account.getId(), account.getAccountSecret());
						_accountId = account.getId();
					}else{
						accountMapper.updateAccount(account);
						_accountId = accountId;
					}
					accountMapper.insertOrUpdateAccountProfile(_accountId,account.getAccountProfile());
					
					if(accountId == null)
						accountRedis.onCreateAccount(account);
					else
						accountRedis.onUpdateAccount(account);
					return null;
				}
				
			});
		}catch(DataIntegrityViolationException ex){
			ex.printStackTrace();
			throw new LotusprizeError(ErrorCode.account_active_email_email_duplicate);
		}
		
	}
	
	public void resetPasswordForce(final Integer accountId,String newPassword){
		final Account account = accountMapper.loadAccountWithSecretByAccountId(accountId);
		PasswordSalt passwordSalt = PasswordSaltGenerator.instance().generate(account.getAccountSecret().getPasswordSalt());
		final AccountSecret newAccountSecret = new AccountSecret(passwordSalt.passwordHashed(newPassword),passwordSalt.saltHex());
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Object run() {
				accountMapper.insertOrUpdateAccountSecret(accountId, newAccountSecret);
				return null;
			}
		});
		SystemEventBus.instance().post(new ShiroRedisAuthenticationCache.RemoveUsernameAuthenticationInfoNotification(account.getEmail()));
	}
	
	
	public Collection<Account> topicManagers(){
		return accountRedis.topicManagerList();
	}
	
	public List<Map<String,Object>> experts(){
        return accountMapper.findAllExpert();
	}
	    
	public List<Map<String,Object>> topicManagers(Integer topicId){
	    return accountMapper.findTopicManager(topicId);
	}
	
}
