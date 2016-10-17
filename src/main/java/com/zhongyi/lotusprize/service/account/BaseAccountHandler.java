package com.zhongyi.lotusprize.service.account;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.mapper.account.AccountMapper;
import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.ITransactionRunner;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.WebUtil;

public abstract class BaseAccountHandler {
	
	@Autowired
	protected AccountRedis accountRedis;
	
	@Autowired
	protected AccountMapper  accountMapper;
	
	@Autowired
	protected ITransactionRunner txRunner; 
	
	public Account currentAccount(){
		Integer accountId = WebUtil.currentUserAccountId();
		if(accountId == null){
			throw new LotusprizeError(ErrorCode.timeout);
		}
		return accountRedis.accountById(accountId);
	}
	
	public Boolean emailExist(String email){
		return accountRedis.isEmailExist(email);
	}
	
	public Account accountById(Integer accountId){
		return accountRedis.accountById(accountId);
	}
	
	public SqlQueryResult<Account> listBy(String fullnameOrEmail,String roleName,
			Integer offset,	Integer limit) {
		Integer roleValue = Roles.roleNameByValue(roleName);
		Integer count = accountMapper.countBy(fullnameOrEmail, roleValue);
		List<Account> accountList = Collections.emptyList();
		if (count > 0) {
			Collection<Integer> accountIdList = accountMapper.findAccountIdBy(fullnameOrEmail,roleValue, offset, limit);
			if (!accountIdList.isEmpty()) {
				accountList = accountRedis.accountByIdList(accountIdList);
			}
		}
		return new SqlQueryResult<Account>(count, accountList);
	}

}
