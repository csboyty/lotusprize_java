package com.zhongyi.lotusprize.auth.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zhongyi.lotusprize.redis.RedisEnable;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.util.serializer.JavaSerializer;
import com.zhongyi.lotusprize.util.serializer.ObjectSerializer;


public class ShiroRedisSessionCache extends RedisEnable implements Cache<String, Session> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final static ThreadLocal<Session> currentThreadSession = new ThreadLocal<Session>();
    private final String name;
    private final int expireInSeconds;
    private final ObjectSerializer<Session> objectSerializer = new JavaSerializer<Session>(Session.class);

    public ShiroRedisSessionCache(String name, int expireInSeconds, JedisPool jedisPool) {
        this.name = name;
        this.expireInSeconds = expireInSeconds;
        this.jedisPool = jedisPool;

    }

    public String name() {
        return name;
    }

    @Override
    public Session get(String sessionId) throws CacheException {
    	Session session = currentThreadSession.get();
    	if(session == null || !sessionId.equals(session.getId())){
	        try {
	            final byte[] keyBytes = keyAsBytes(sessionId);
	            byte[] valueBytes = (byte[]) execute(new RedisOperation() {
	                @Override
	                public Object doWithRedis(Jedis jedis) {
	
	                    byte[] bytes = jedis.get(keyBytes);
	                    jedis.expire(keyBytes, expireInSeconds);
	                    return bytes;
	                }
	            });
	            
	            session = objectSerializer.unmarshall(valueBytes);
	            currentThreadSession.set(session);
	            return session;
	        } catch (Exception ex) {
	            throw new CacheException(ex);
	        }
    	}else{
    		return session;
    	}
    }

    private final byte[] keyAsBytes(String key) {
        byte[] keyBytes = key.getBytes(ApplicationProperty.instance().charset());
        return keyBytes;

    }

    @Override
    public Session put(String sessionId, Session session) throws CacheException {
    	currentThreadSession.set(session);
    	return session;
    }

    @Override
    public Session remove(String sessionId) throws CacheException {
        try {
            final byte[] keyBytes = keyAsBytes(sessionId);
            execute(new RedisOperation() {
                @Override
                public Object doWithRedis(Jedis jedis) {
                    return jedis.del(keyBytes);
                }

            });
        } catch (Exception ex) {
            throw new CacheException(ex);
        }
        return null;
    }


    @Override
    public void clear() throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Session> values() {
        throw new UnsupportedOperationException();
    }

	public void updateSession() {
		Session session = currentThreadSession.get();
		if(session != null){
			try {
	            final byte[] keyBytes = keyAsBytes(session.getId().toString());
	            final byte[] valueBytes = objectSerializer.marshall(session);
	            execute(new RedisOperation() {
	                @Override
	                public Void doWithRedis(Jedis jedis) {
	                    jedis.set(keyBytes, valueBytes);
	                    jedis.expire(keyBytes, expireInSeconds);
	                    return null;
	                }
	            });
	        } catch (Exception ex) {
	        	logger.error("保存session出错",ex);
	        }finally{
	        	currentThreadSession.remove();
	        }
		}
	}


}

