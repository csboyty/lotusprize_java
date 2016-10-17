package com.zhongyi.lotusprize.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.JsonUtil;

@Component
public class AccountRedis extends RedisEnable {
	
	public static String accountKey(Integer accountId){
		return String.format("lp:account:%d", accountId);
	}
	
	public static String accountKey(String accountId){
		return "lp:account:"+accountId;
	}
	
	public static String accountEmails(){
		return "lp:account-email:set";
	}
	
	public static String topicManagerListKey(){
		return "lp:topic-manager:list";
	}
	
	public static String accountArtifactKey(Integer accountId){
	    return accountArtifactKey(String.valueOf(accountId));
	}
	
	public static String accountArtifactKey(String accountId){
        return String.format("lp:account-artifact-set:" + accountId);
    }
	
	
	public void onCreateAccount(final Account account){
		final String accountKey = accountKey(account.getId());
		final String accountJson = JsonUtil.toJsonString(account);
		execute(new RedisOperation(){
			@Override
			public Void doWithRedis(Jedis jedis) {
				Transaction tx = jedis.multi();
				tx.set(accountKey, accountJson);
				tx.sadd(accountEmails(), account.getEmail());
				if(account.getRoleValue() == Roles.topicManagerRole.value()){
					tx.rpush(AccountRedis.topicManagerListKey(), String.valueOf(account.getId()));
				}
				tx.exec();
				return null;
			}
		});
	}
	
	public void onUpdateAccount(final Account account){
		final String accountKey = accountKey(account.getId());
		final String accountJson = JsonUtil.toJsonString(account);
		execute(new RedisOperation(){
			@Override
			public Void doWithRedis(Jedis jedis) {
				Transaction tx = jedis.multi();
				tx.set(accountKey, accountJson);
				tx.exec();
				return null;
			}
		});
	}
	
	public void onDeleteAccount(final Integer accountId){
		execute(new RedisOperation(){
			@Override
			public Void doWithRedis(Jedis jedis) {
				final String accountKey = accountKey(accountId);
				Transaction tx = jedis.multi();
				tx.del(accountKey);
				tx.lrem(AccountRedis.topicManagerListKey(), 0, String.valueOf(accountId));
				tx.exec();
				return null;
			}
		});
	}
	
	
	public Boolean isEmailExist(final String email){
		return (Boolean)execute(new RedisOperation(){
			
			public Boolean doWithRedis(Jedis jedis){
				return jedis.sismember(accountEmails(), email);
			}
		});
	}
	
	public Account accountById(final Integer accountId){
		final String accountKey = accountKey(accountId);
		String accountJson = (String)execute(new RedisOperation(){
			@Override
			public String doWithRedis(Jedis jedis) {
				return jedis.get(accountKey);
			}
		});
		Account account = null;
		if(!Strings.isNullOrEmpty(accountJson)){
			account = JsonUtil.fromJson(new TypeReference<Account>(){}, accountJson);
		}
		return account;
	}
	
	public List<Account> accountByIdList(final Collection<Integer> accountIdList){
		final List<String> accountKeyList = Lists.newArrayListWithExpectedSize(accountIdList.size());
		for(Integer accountId:accountIdList){
			accountKeyList.add(accountKey(accountId));
		}
		@SuppressWarnings("unchecked")
		List<String> accountJsonList = (List<String>)execute(new RedisOperation(){
			@Override
			public List<String> doWithRedis(Jedis jedis) {
				return jedis.mget(accountKeyList.toArray(new String[0]));
			}
		});
		List<Account> accounts= Lists.newArrayListWithExpectedSize(accountIdList.size());
		for(String accountJson:accountJsonList){
			if(!Strings.isNullOrEmpty(accountJson)){
				Account account = JsonUtil.fromJson(new TypeReference<Account>(){}, accountJson);
				accounts.add(account);
			}
		}
		return accounts;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<Account> topicManagerList(){
		return (Collection<Account>)execute(new RedisOperation(){
			@Override
			public Collection<Account> doWithRedis(Jedis jedis) {
				List<String> topicManagerIdList =jedis.lrange(AccountRedis.topicManagerListKey(), 0, -1);
				if(!topicManagerIdList.isEmpty()){
					List<String> accountKeyList = Lists.newArrayListWithExpectedSize(topicManagerIdList.size());
					for(String accountId:topicManagerIdList){
						accountKeyList.add(accountKey(accountId));
					}
					Collection<Account> topicManagerList = Lists.newArrayListWithCapacity(topicManagerIdList.size());
					
					for(String topicManagerJson:jedis.mget(accountKeyList.toArray(new String[0]))){
						if(!Strings.isNullOrEmpty(topicManagerJson)){
							Account topicManager= JsonUtil.fromJson(new TypeReference<Account>(){}, topicManagerJson);
							topicManagerList.add(topicManager);
						}
					}
					return topicManagerList;
				}
				return Collections.EMPTY_LIST;
			}
		
		});
	}
	

}
