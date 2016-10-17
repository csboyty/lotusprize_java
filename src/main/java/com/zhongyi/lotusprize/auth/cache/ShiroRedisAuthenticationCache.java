package com.zhongyi.lotusprize.auth.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.eventbus.Subscribe;
import com.zhongyi.lotusprize.auth.LotusprizePrincipal;
import com.zhongyi.lotusprize.redis.RedisEnable;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.SystemEventBus;
import com.zhongyi.lotusprize.util.serializer.JavaSerializer;
import com.zhongyi.lotusprize.util.serializer.ObjectSerializer;


public class ShiroRedisAuthenticationCache extends RedisEnable implements Cache<Object, AuthenticationInfo> {

    private final byte[] nameBytes;
    private final ObjectSerializer<AuthenticationInfo> objectSerializer = new JavaSerializer<AuthenticationInfo>(AuthenticationInfo.class);

    public ShiroRedisAuthenticationCache(String name, JedisPool jedisPool) {
        this.nameBytes = name.getBytes(ApplicationProperty.instance().charset());
        this.jedisPool = jedisPool;
        SystemEventBus.instance().register(this);
    }

    @Override
    public AuthenticationInfo get(Object authcTokenId) throws CacheException {
        String key = authcTokenId.toString();
        final byte[] keyBytes = keyAsBytes(key);
        try {
            byte[] valueBytes = (byte[]) execute(new RedisOperation() {
                @Override
                public Object doWithRedis(Jedis jedis) {
                    return jedis.hget(nameBytes, keyBytes);
                }
            });
            return objectSerializer.unmarshall(valueBytes);
        } catch (Exception ex) {
            throw new CacheException(ex);
        }
    }

    private final byte[] keyAsBytes(String key) {
        byte[] keyBytes = key.getBytes(ApplicationProperty.instance().charset());
        return keyBytes;

    }

    @Override
    public AuthenticationInfo put(Object authcTokenId, AuthenticationInfo value) throws CacheException {
        String key = authcTokenId.toString();
        final byte[] keyBytes = keyAsBytes(key);
        try {
            final byte[] valueBytes = objectSerializer.marshall(value);
            execute(new RedisOperation() {

                @Override
                public Object doWithRedis(Jedis jedis) {
                    jedis.hset(nameBytes, keyBytes, valueBytes);
                    return null;
                }

            });

        } catch (Exception ex) {
            throw new CacheException(ex);
        }
        return null;
    }

    @Override
    public AuthenticationInfo remove(Object principal) throws CacheException {
        LotusprizePrincipal _principal = (LotusprizePrincipal) principal;
        String key = _principal.getUid();
        final byte[] keyBytes = keyAsBytes(key);
        try {
            execute(new RedisOperation() {

                @Override
                public Object doWithRedis(Jedis jedis) {
                    return jedis.hdel(nameBytes, keyBytes);
                }
            });
        } catch (Exception ex) {
            throw new CacheException(ex);
        }

        return null;
    }

    @Override
    public void clear() throws CacheException {
        try {
            execute(new RedisOperation() {

                @Override
                public Object doWithRedis(Jedis jedis) {
                    return jedis.del(nameBytes);
                }

            });
        } catch (Exception ex) {
            throw new CacheException(ex);
        }


    }

    @Override
    public int size() {
        long size = (long) execute(new RedisOperation() {

            @Override
            public Object doWithRedis(Jedis jedis) {
                return jedis.hlen(nameBytes);
            }

        });
        if (size < Integer.MIN_VALUE || size > Integer.MAX_VALUE) {
            throw new CacheException(size + " cannot be cast to int without changing its value.");
        }
        return (int) size;
    }

    @Override
    public Set<Object> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<AuthenticationInfo> values() {
        throw new UnsupportedOperationException();
    }
    
    
    @Subscribe
    public void onRemoveUsernameAuthenticationInfoNotification(RemoveUsernameAuthenticationInfoNotification notification){
    	LotusprizePrincipal principal = new LotusprizePrincipal(notification.getUsername(),null);
    	remove(principal);
    }
    
    public static class RemoveUsernameAuthenticationInfoNotification {
    	
    	private final String username;
    	
    	public RemoveUsernameAuthenticationInfoNotification(String username){
    		this.username = username;
    	}

		public String getUsername() {
			return username;
		}
    	
    }
    
 


}

