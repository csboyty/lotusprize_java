package com.zhongyi.lotusprize.email;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.auth.PasswordSaltGenerator;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.account.InactiveAccount;
import com.zhongyi.lotusprize.domain.email.AccountActiveEmail;
import com.zhongyi.lotusprize.domain.email.AccountResetPasswordEmail;
import com.zhongyi.lotusprize.domain.email.EmailBody;
import com.zhongyi.lotusprize.domain.email.EmailItem;
import com.zhongyi.lotusprize.mapper.email.EmailMapper;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.Ciphers;
import com.zhongyi.lotusprize.util.ObjectId;

public class EmailBuilder {
	
	
	private final VelocityEngine velocityEngine;
	
	private String activeUriFormat;
	
	private String resetPasswordUriFormat;
	
	@Autowired
	private EmailMapper emailMapper;
	
	public EmailBuilder(VelocityEngine velocityEngine){
		this.velocityEngine = velocityEngine;
	}
	
	@PostConstruct
	public void init(){
		activeUriFormat = ApplicationProperty.instance()
				.getAsString("context.url")+ "/s/account/active?inactiveAccountId=%s&email=%s&token=%s&time=%s&_lang=%s";
		resetPasswordUriFormat = ApplicationProperty.instance()
				.getAsString("context.url")+"/s/account/resetPassword?accountId=%s&token=%s&time=%s&_lang=%s";
	}
	
	public EmailItem newActiveEmail(InactiveAccount inactiveAccount,Date registerTime,String lang){
		EmailItem emailItem = new EmailItem();
		emailItem.setAddress(inactiveAccount.getEmail());
		emailItem.setCreateTime(registerTime);
		emailItem.setType(EmailItem.Type.confirm);
		emailItem.setStatus(EmailItem.Status.waiting);
		emailItem.setServerId(ApplicationProperty.instance().getAsString("server.id"));
		
		String token = ObjectId.get().toString();
		String activeUri = String.format(activeUriFormat,
				Ciphers.confirmEmailCipher.doEncryptUri(String.valueOf(inactiveAccount.getId())),
				Ciphers.confirmEmailCipher.doEncryptUri(inactiveAccount.getEmail()),
				token,
				Ciphers.confirmEmailCipher.doEncryptUri(String.valueOf(registerTime.getTime())),
				lang);
		Map<String,Object> map = Maps.newHashMap();
		map.put("fullname",inactiveAccount.getFullname());
		map.put("email",inactiveAccount.getEmail());
		map.put("url",activeUri);
		
		String title;
		String tpl;
		if("zh".equals(lang)){
			title = "确认您在芙蓉杯的邮箱";
			tpl="account_active.html";
		}else{
			title = "Confirm your email at lotusprize";
			tpl = "account_active_en.html";
		}
		
		
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,tpl,ApplicationProperty.instance().encoding(),map);
		emailItem.setBody(new EmailBody(title,text,null));
		
		emailMapper.insertEmailItem(emailItem);
		emailMapper.insertEmailBody(emailItem.getId(),emailItem.getBody());
		AccountActiveEmail accountConfirmEmail = new AccountActiveEmail(inactiveAccount.getId(),
				emailItem.getId(),token);
		emailMapper.insertAccountActiveEmail(accountConfirmEmail);
		return emailItem;
	}
	
	public EmailItem newResetPasswordEmail(Account account,Date resetPasswordTime,String lang){
		EmailItem emailItem = new EmailItem();
		emailItem.setAddress(account.getEmail());
		emailItem.setCreateTime(resetPasswordTime);
		emailItem.setType(EmailItem.Type.reset_password);
		emailItem.setStatus(EmailItem.Status.waiting);
		emailItem.setServerId(ApplicationProperty.instance().getAsString("server.id"));
		
		String token = ObjectId.get().toString();
		String resetPasswordUri = String.format(resetPasswordUriFormat,
				Ciphers.resetPasswordCipher.doEncryptUri(String.valueOf(account.getId())),
				token,
				Ciphers.resetPasswordCipher.doEncryptUri(String.valueOf(resetPasswordTime.getTime())),
				lang);
		
		String newPassword = RandomStringUtils.randomAlphanumeric(10);
		Map<String,Object> map = Maps.newHashMap();
		map.put("fullname",account.getFullname());
		map.put("newPassword", newPassword);
		map.put("url",resetPasswordUri);
		
		String title;
		String tpl;
		if("zh".equals(lang)){
			title = "重置您在芙蓉杯的密码";
			tpl="reset_password.html";
		}else{
			title = "Reset your password at lotusprize";
			tpl = "reset_password_en.html";
		}
		
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,tpl,ApplicationProperty.instance().encoding(),map);
		emailItem.setBody(new EmailBody(title,text,null));
		
		emailMapper.insertEmailItem(emailItem);
		emailMapper.insertEmailBody(emailItem.getId(),emailItem.getBody());
		
		String newHashedPassword = PasswordSaltGenerator.instance().generate(account.getAccountSecret().getPasswordSalt()).passwordHashed(newPassword);
		AccountResetPasswordEmail accountResetPasswordEmail = new AccountResetPasswordEmail(account.getId(),
				emailItem.getId(),newHashedPassword,token);
		
		emailMapper.insertOrUpdateAccountResetPasswordEmail(accountResetPasswordEmail);
		return emailItem;
	}

}
