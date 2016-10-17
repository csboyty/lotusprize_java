package com.zhongyi.lotusprize.mapper.account;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.account.AccountProfile;
import com.zhongyi.lotusprize.domain.account.AccountSecret;

/**
 * Created by zzy on 14-2-21.
 */
public interface AccountMapper {

	List<Account> iterAccountWithProfile(
			@Param(value = "baseAccountId") int baseAccountId,
			@Param(value = "limit") int limit);

	Account loadAccountWithProfileByAccountId(Integer accountId);

	Account loadAccountWithSecretByEmail(String email);
	
	Account loadAccountWithSecretByAccountId(Integer accountId);

	int insertAccount(Account account);
	
	int updateAccount(Account account);
	
	int deleteAccountById(Integer id);

	int insertOrUpdateAccountProfile(
			@Param(value = "accountId") Integer accountId,
			@Param(value = "accountProfile") AccountProfile accountProfile);
	
	

	int insertOrUpdateAccountSecret(
			@Param(value = "accountId") Integer accountId,
			@Param(value = "accountSecret") AccountSecret accountSecret);

	int accountRoleByAccountId(Integer accountId);
	
	
	int countBy(@Param("fullnameOrEmail")String fullnameOrEmail,@Param("roleValue")Integer roleValue);
	
	Collection<Integer> findAccountIdBy(@Param("fullnameOrEmail")String fullnameOrEmail,@Param("roleValue")Integer roleValue,
	        @Param("offset") Integer offset,@Param("limit") Integer limit);
	
	
	List<Map<String,Object>> findAllExpert();
	
	List<Map<String,Object>> findTopicManager(Integer topicId);
	

}
