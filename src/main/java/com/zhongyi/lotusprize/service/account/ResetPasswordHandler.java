package com.zhongyi.lotusprize.service.account;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhongyi.lotusprize.auth.cache.ShiroRedisAuthenticationCache;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.email.AccountResetPasswordEmail;
import com.zhongyi.lotusprize.email.EmailBuilder;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.mapper.email.EmailMapper;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.SystemEventBus;

@Component
public class ResetPasswordHandler extends BaseAccountHandler{
	
	@Autowired
	private EmailBuilder emailBuilder;
	
	@Autowired
	private EmailMapper emailMapper;
	
	
	public void sendResetPasswordEmail(final String email,final String lang){
		final Date now = new Date();
		final Account account = accountMapper.loadAccountWithSecretByEmail(email);
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Object run() {
				emailBuilder.newResetPasswordEmail(account,now,lang);
				return null;
			}
			
		});
	}
	
	
	public void resetPassword(final Integer accountId,final String token){
		AccountResetPasswordEmail accountResetPasswordEmail = emailMapper.accountResetPasswordEmailByAccountId(accountId);
		if(accountResetPasswordEmail == null){
			throw new LotusprizeError(ErrorCode.account_reset_password_email_not_exist);
		}
		if(! accountResetPasswordEmail.getToken().equals(token)){
			throw new LotusprizeError(ErrorCode.account_reset_password_email_token_not_match);
		}
		
		final Account account = accountMapper.loadAccountWithSecretByAccountId(accountId);
		account.getAccountSecret().setPassword(accountResetPasswordEmail.getPassword());
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Object run() {
				accountMapper.insertOrUpdateAccountSecret(accountId, account.getAccountSecret());
				emailMapper.deleteAccountResetPasswordEmailByAccountIdAndToken(accountId,token);
				return null;
			}
			
		});
		SystemEventBus.instance().post(new ShiroRedisAuthenticationCache.
				RemoveUsernameAuthenticationInfoNotification(account.getEmail()));
		
	}
	

}
