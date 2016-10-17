package com.zhongyi.lotusprize.service.topic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;

@Component
public class StageSettingHandler {
    
    public final static short stage_pending = 1;
    
    public final static short stage_pass = 2;
    
    public final static short stage_nopass = 3;
    
    public final static short stage_round1 = 4;
    
    private static StageSettingHandler _instance;
    
    @Autowired
    private TopicRedis topicRedis;
    
    
    @PostConstruct
    public void init(){
        _instance = this;
    }
    
    
    public Integer current(){
        return (Integer)topicRedis.execute(new RedisOperation(){
            @Override
            public Integer doWithRedis(Jedis jedis) {
                String stageSettingKey=TopicRedis.stageSettingKey();
                String stageSettingValue = jedis.get(stageSettingKey);
                if(stageSettingValue == null){
                    return null;
                }else{
                    return Integer.parseInt(stageSettingValue);
                }
            }
        });
        
    }
    
    public void setStageSetting(final Integer stage){
        if(stage != null){
            topicRedis.execute(new RedisOperation(){
                public Object doWithRedis(Jedis jedis) {
                    String stageSettingKey=TopicRedis.stageSettingKey();
                    jedis.set(stageSettingKey,String.valueOf(stage));
                    return null;
                }
            });
        }
    }
    
    
    public short roundByStage(){
        Integer stage = current();
        if(stage !=null && stage >=8){
            return 2;
        }
        return 1;
    }
    
    
    public static StageSettingHandler getInstance(){
        return _instance;
    }
    

}
