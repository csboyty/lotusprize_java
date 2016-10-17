package com.zhongyi.lotusprize.service.artifact;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.WebUtil;

@Component
public class ArtifactUserPermissionHandler {
    
    @Autowired
    private AccountRedis accountRedis;
    
    
    public boolean hasReadPermision(final Integer artifactId){
//        boolean readable = false;
//        if(SecurityUtils.getSubject().isAuthenticated()){
//            if((Roles.adminRole.value() & WebUtil.currentUserRoleValue()) !=0){
//                readable = true;
//            }else{
//                final Integer accountId =WebUtil.currentUserAccountId();
//                readable = (Boolean)accountRedis.execute(new RedisOperation(){
//                    @Override
//                    public Boolean doWithRedis(Jedis jedis) {
//                        return jedis.sismember(AccountRedis.accountArtifactKey(accountId), String.valueOf(artifactId));
//                    }
//                    
//                });
//            }
//            
//        }
//        return readable;
        return true;
        
    }

}
