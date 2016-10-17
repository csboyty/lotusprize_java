package com.zhongyi.lotusprize.redis;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public abstract class RedisEnable {
	
	@Autowired
	protected JedisPool jedisPool;

	public Object execute(RedisOperation operation) {
		Jedis jedis = jedisPool.getResource();
		boolean borrowOrOprSuccess = true;
		try {
			return operation.doWithRedis(jedis);
		} catch (JedisConnectionException e) {
			borrowOrOprSuccess = false;
			if (jedis != null)
				jedisPool.returnBrokenResource(jedis);
			throw e;
		} finally {
			if (borrowOrOprSuccess)
				jedisPool.returnResource(jedis);
		}
	}

	public interface RedisOperation {
		public Object doWithRedis(Jedis jedis);
	}

}
