package com.zhongyi.lotusprize.service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.zhongyi.lotusprize.auth.PasswordSaltGenerator;
import com.zhongyi.lotusprize.auth.PasswordSaltGenerator.PasswordSalt;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.account.AccountProfile;
import com.zhongyi.lotusprize.domain.account.AccountSecret;
import com.zhongyi.lotusprize.domain.account.InactiveAccount;
import com.zhongyi.lotusprize.domain.email.AccountActiveEmail;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.mapper.account.InactiveAccountMapper;
import com.zhongyi.lotusprize.mapper.email.EmailMapper;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.role.Roles;

@Component
public class ActiveAccountHandler extends BaseAccountHandler{
	
	
	@Autowired
	private EmailMapper emailMapper;
	
	@Autowired
	private InactiveAccountMapper inactiveAccountMapper;
	
	
	public void activeUser(final Integer inactiveAccountId,final String email,final String token){
		AccountActiveEmail accountActiveEmail = emailMapper.accountActiveEmailByInactiveAccountId(inactiveAccountId);
		if(accountActiveEmail == null){
			throw new LotusprizeError(ErrorCode.account_active_email_not_exist);
		}
		if(! accountActiveEmail.getToken().equals(token)){
			throw new LotusprizeError(ErrorCode.account_active_email_token_not_match);
		}
		
		final InactiveAccount inactiveAccount = inactiveAccountMapper.loadById(inactiveAccountId);
		final Account account = toAccount(inactiveAccount);
		
		try{
			txRunner.doInTransaction(new ITransactionOperation(){
				@Override
				public Void run() {
					emailMapper.deleteAccountActiveEmailByInactiveAccounId(inactiveAccountId);
					accountMapper.insertAccount(account);
					accountMapper.insertOrUpdateAccountSecret(account.getId(), account.getAccountSecret());
					accountMapper.insertOrUpdateAccountProfile(account.getId(), account.getAccountProfile());
					accountRedis.onCreateAccount(account);
					return null;
				}
				
			});
		}catch(DataIntegrityViolationException ex){
			ex.printStackTrace();
			throw new LotusprizeError(ErrorCode.account_active_email_email_duplicate);
		}
		
	}
	
	
	private Account toAccount(InactiveAccount inactiveAccount){
		final Account account = new Account();
		account.setEmail(inactiveAccount.getEmail());
		account.setFullname(inactiveAccount.getFullname());
		account.setMobile(inactiveAccount.getMobile());
		account.setAddress(inactiveAccount.getAddress());
		account.setRoleValue(Roles.userRole.value());
		account.setStatus(Account.Status.active);
		PasswordSalt passwordSalt = PasswordSaltGenerator.instance().generate();
		final AccountSecret accountSecret = new AccountSecret();
		accountSecret.setPassword(passwordSalt.passwordHashed(inactiveAccount.getPassword()));
		accountSecret.setPasswordSalt(passwordSalt.saltHex());
		account.setAccountSecret(accountSecret);
		final AccountProfile accountProfile = new AccountProfile();
		accountProfile.setGender((byte)1);
		accountProfile.setOrganization(inactiveAccount.getOrganization());
		account.setAccountProfile(accountProfile);
		return account;
	}


}
