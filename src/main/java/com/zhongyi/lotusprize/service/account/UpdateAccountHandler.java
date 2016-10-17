package com.zhongyi.lotusprize.service.account;

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
import com.zhongyi.lotusprize.util.WebUtil;

@Component
public class UpdateAccountHandler extends BaseAccountHandler{
	
	
	public void changeInfo(String fullname,String mobile,String address,
			Byte gender,String memo,String organization){
		final Account account = currentAccount();
		account.setFullname(fullname);
		account.setMobile(mobile);
		account.setAddress(address);
		final AccountProfile accountProfile = toAccountProfile(gender,memo,organization);
		account.setAccountProfile(accountProfile);
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Object run() {
				accountMapper.updateAccount(account);
				accountMapper.insertOrUpdateAccountProfile(account.getId(), accountProfile);
				accountRedis.onUpdateAccount(account);
				return null;
			}
		});
	}
	
	
	public void changePassword(String oldPassword,String newPassword){
		final Integer accountId = WebUtil.currentUserAccountId();
		Account account = accountMapper.loadAccountWithSecretByAccountId(accountId);
		PasswordSalt passwordSalt = PasswordSaltGenerator.instance().generate(account.getAccountSecret().getPasswordSalt());
		if(! passwordSalt.passwordHashed(oldPassword).equals(account.getAccountSecret().getPassword())){
			throw new LotusprizeError(ErrorCode.account_password_not_match);
		}
		final AccountSecret newAccountSecret = new AccountSecret(passwordSalt.passwordHashed(newPassword),passwordSalt.saltHex());
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Object run() {
				accountMapper.insertOrUpdateAccountSecret(accountId, newAccountSecret);
				return null;
			}
		});
		SystemEventBus.instance().post(new ShiroRedisAuthenticationCache.
				RemoveUsernameAuthenticationInfoNotification(account.getEmail()));
		
	}
	
	private AccountProfile toAccountProfile(Byte gender,String memo,String organization){
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setGender(gender);
		accountProfile.setMemo(memo);
		accountProfile.setOrganization(organization);
		return accountProfile;
	}

}
