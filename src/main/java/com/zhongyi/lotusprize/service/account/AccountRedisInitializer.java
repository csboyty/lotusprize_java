package com.zhongyi.lotusprize.service.account;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.mapper.account.AccountMapper;
import com.zhongyi.lotusprize.redis.AccountRedis;

public class AccountRedisInitializer {

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private AccountRedis accountRedis;
	
	@PostConstruct
	public void init(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				AccountIterator it = new AccountIterator(0,50);
				while(it.hasNext()){
					Account account = it.next();
					accountRedis.onCreateAccount(account);
				}
			}
		}).start();
		
	}
	

	private class AccountIterator implements Iterator<Account> {

		private final int limit;
		private int offsetAccountId;
		private Iterator<Account> _iterator;

		private AccountIterator(int offsetAccountId, int limit) {
			this.offsetAccountId = offsetAccountId;
			this.limit = limit;
			_iterator = accountMapper.iterAccountWithProfile(offsetAccountId,
					limit).iterator();
		}

		@Override
		public boolean hasNext() {
			boolean _hasNext = _iterator.hasNext();
			if (!_hasNext) {
				_iterator = accountMapper.iterAccountWithProfile(
						offsetAccountId, limit).iterator();
				_hasNext = _iterator.hasNext();
			}
			return _hasNext;
		}

		@Override
		public Account next() {
			Account account = _iterator.next();
			offsetAccountId = account.getId();
			return account;
		}

		@Override
		public void remove() {

		}
	}

}
