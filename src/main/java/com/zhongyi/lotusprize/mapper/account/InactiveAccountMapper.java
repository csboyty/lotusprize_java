package com.zhongyi.lotusprize.mapper.account;

import java.util.Date;

import com.zhongyi.lotusprize.domain.account.InactiveAccount;

public interface InactiveAccountMapper {
	
	InactiveAccount loadById(Integer id);
	
	int insertInActiveAccount(InactiveAccount inactiveAccount);
	
	int countByEmail(String email);
	
	int deleteById(Integer id);
	
	int deleteExpired(Date expireTime);

}
