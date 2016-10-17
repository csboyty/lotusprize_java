package com.zhongyi.lotusprize.service.artifact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.zhongyi.lotusprize.mapper.artifact.Expert2ArtifactMapper;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.service.topic.StageSettingHandler;

@Component
public class ArtifactStatusHandler extends BaseArtifactHandler{
	
	@Autowired
	private Expert2ArtifactMapper expert2ArtifactMapper;
	
	public void updateArtifactStatus(final Iterable<Integer> artifactIdIter,final Short status){
	    final short round =1;
	    for(final Integer artifactId:artifactIdIter){
    		txRunner.doInTransaction(new ITransactionOperation(){
    			@Override
    			public Object run() {
    			    artifactMapper.updateStatus(artifactIdIter,status);
    				artifactRedis.execute(new RedisOperation(){
    					@Override
    					public Void doWithRedis(Jedis jedis) {
    					    String key = ArtifactRedis.artifactKey(artifactId);
    						short oldStatus = Short.parseShort(jedis.hget(key, "status"));
    						if(StageSettingHandler.stage_round1 == oldStatus && StageSettingHandler.stage_round1 !=status){
    						    expert2ArtifactMapper.deleteArtifact2ExpertScoreByArtifactIdAndRound(artifactId, round);
    						}
    						String statusString = String.valueOf(status);
							jedis.hset(key, "status", statusString);
    						return null;
    					}
    					
    				});
    				return null;
    			}
    		});
	    }
	}
	
	
	
	
	

	
	

}
