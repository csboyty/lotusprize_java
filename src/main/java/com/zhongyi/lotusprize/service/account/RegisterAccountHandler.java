package com.zhongyi.lotusprize.service.account;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhongyi.lotusprize.domain.account.InactiveAccount;
import com.zhongyi.lotusprize.email.EmailBuilder;
import com.zhongyi.lotusprize.mapper.account.InactiveAccountMapper;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.util.DateTimeUtil;

@Component
public class RegisterAccountHandler extends BaseAccountHandler{
	
	@Autowired
	private InactiveAccountMapper inactiveAccountMapper;
	
	@Autowired
	private EmailBuilder emailBuilder;
	
	private ScheduledExecutorService removeExpiredInactiveAccountExecutor;
	
	@PostConstruct
	public void init(){
		removeExpiredInactiveAccountExecutor = Executors.newSingleThreadScheduledExecutor();
		removeExpiredInactiveAccountExecutor.scheduleWithFixedDelay(
				new RemoveExpiredInactiveAccountTask(), 0, 1, TimeUnit.DAYS);
	}
	
	
	public void registerUser(final String fullname,final String email,final String password,
			final String mobile,final String address,String organization,final String lang){
		final Date now = new Date();
		final InactiveAccount inactiveAccount = new InactiveAccount();
		inactiveAccount.setFullname(fullname);
		inactiveAccount.setEmail(email);
		inactiveAccount.setPassword(password);
		inactiveAccount.setMobile(mobile);
		inactiveAccount.setAddress(address);
		inactiveAccount.setOrganization(organization);
		inactiveAccount.setCreateTime(now);
		
		txRunner.doInTransaction(new ITransactionOperation(){
			@Override
			public Void run() {
				inactiveAccountMapper.insertInActiveAccount(inactiveAccount);
				emailBuilder.newActiveEmail(inactiveAccount,now,lang);
				return null;
			}
		});
		
	}
	
	private class RemoveExpiredInactiveAccountTask implements Runnable{

		@Override
		public void run() {
			long lastDayInMills = System.currentTimeMillis() - DateTimeUtil.one_day_mills;
			inactiveAccountMapper.deleteExpired(new Date(lastDayInMills));
		}
		
	}
	

}
