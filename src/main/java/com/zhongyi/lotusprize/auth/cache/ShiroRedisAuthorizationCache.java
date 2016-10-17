package com.zhongyi.lotusprize.auth.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.eventbus.Subscribe;
import com.zhongyi.lotusprize.auth.LotusprizePrincipal;
import com.zhongyi.lotusprize.auth.UsernameAndPasswordRealm;
import com.zhongyi.lotusprize.redis.RedisEnable;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.SystemEventBus;
import com.zhongyi.lotusprize.util.serializer.JavaSerializer;
import com.zhongyi.lotusprize.util.serializer.ObjectSerializer;


public class ShiroRedisAuthorizationCache extends RedisEnable implements Cache<PrincipalCollection, AuthorizationInfo> {

    private final byte[] nameBytes;
    private final ObjectSerializer<AuthorizationInfo> objectSerializer = new JavaSerializer<AuthorizationInfo>(AuthorizationInfo.class);

    public ShiroRedisAuthorizationCache(String name,JedisPool jedisPool) {
        this.nameBytes = name.getBytes(ApplicationProperty.instance().charset());
        this.jedisPool = jedisPool;
        SystemEventBus.instance().register(this);
    }

    @Override
    public AuthorizationInfo get(PrincipalCollection key) throws CacheException {
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

    @Override
    public AuthorizationInfo put(PrincipalCollection key, AuthorizationInfo value) throws CacheException {
        final byte[] keyBytes = keyAsBytes(key);
        try {
            final byte[] valueBytes = objectSerializer.marshall(value);
            execute(new RedisOperation() {

                @Override
                public Object doWithRedis(Jedis jedis) {
                    return jedis.hset(nameBytes, keyBytes, valueBytes);
                }

            });
        } catch (Exception ex) {
            throw new CacheException(ex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private byte[] keyAsBytes(PrincipalCollection key) {
        Collection<LotusprizePrincipal> principals = key.fromRealm(UsernameAndPasswordRealm.REALM_NAME);
        if (principals != null && !principals.isEmpty()) {
            int accountId = principals.iterator().next().getAccountId();
            return String.valueOf(accountId).getBytes(ApplicationProperty.instance().charset());
        }
        throw new IllegalStateException("PrincipalCollection:" + key);

    }

    @Override
    public AuthorizationInfo remove(PrincipalCollection key)
            throws CacheException {
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
                    jedis.del(nameBytes);
                    return null;
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
    public Set<PrincipalCollection> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<AuthorizationInfo> values() {
        throw new UnsupportedOperationException();
    }

    @Subscribe
    public void onRemoveAuthorizationInfoNotification(RemoveAuthorizationInfoNotification notification){
    	SimplePrincipalCollection principalCollection = 
    			new SimplePrincipalCollection(new LotusprizePrincipal(null,notification.getAccountId()),UsernameAndPasswordRealm.REALM_NAME);
    	remove(principalCollection);
    }
    
    
    public static class RemoveAuthorizationInfoNotification {
    	
    	private final Integer accountId;
    	
    	public RemoveAuthorizationInfoNotification(Integer accountId){
    		this.accountId = accountId;
    	}

		public Integer getAccountId() {
			return accountId;
		}
    	
    }
    
   

}

