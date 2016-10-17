package com.zhongyi.lotusprize.service.artifact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.zhongyi.lotusprize.mapper.artifact.ArtifactResultMapper;
import com.zhongyi.lotusprize.mapper.artifact.ArtifactVoteMapper;
import com.zhongyi.lotusprize.mapper.artifact.Expert2ArtifactMapper;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.service.ITransactionOperation;

@Component
public class ArtifactResultHandler extends BaseArtifactHandler{
    
    @Autowired
    private ArtifactResultMapper artifactResultMapper;
    
    @Autowired
    private ArtifactVoteMapper artifactVoteMapper;
    
    @Autowired
    private Expert2ArtifactMapper expert2ArtifactScoreMapper;
    
    
    public void updateArtifactPrize(final Iterable<Integer> artifactIdIter,final Integer awardsValue){
        txRunner.doInTransaction(new ITransactionOperation(){
            @Override
            public Void run() {
                for(Integer artifactId:artifactIdIter){
                    artifactResultMapper.updatePrize(artifactId, awardsValue);
                    if(awardsValue ==-2 || awardsValue==-4 || awardsValue==-8){
                        expert2ArtifactScoreMapper.deleteArtifact2ExpertScoreByArtifactIdAndRound(artifactId, (short)2);
                    }
                }
                
                
                return null;
            }
            
        });
    }
    
    public boolean isVoteExist(final Integer artifactId,final String clientId){
        return (Boolean)artifactRedis.execute(new RedisOperation(){
            @Override
            public Boolean doWithRedis(Jedis jedis) {
                return jedis.sismember(ArtifactRedis.artifactVoteKey(artifactId), clientId);
            }
            
        });
    }

    public void updateArtifactVote(final Integer artifactId,final String clientId){
        Long newVote =(Long) artifactRedis.execute(new RedisOperation(){
            @Override
            public Long doWithRedis(Jedis jedis) {
                return jedis.sadd(ArtifactRedis.artifactVoteKey(artifactId), clientId);
            }
            
        });
        if(newVote == 1){
            txRunner.doInTransaction(new ITransactionOperation(){
                @Override
                public Void run(){
                    artifactVoteMapper.insertArtifactVote(artifactId, clientId);
                    artifactResultMapper.updateVote(artifactId, 1);
                    return null;
                }
                
            });
        }
    }
    
    public void updateArtifactHatch(final Iterable<Integer> artifactIdIter,final Short hatchValue){
        txRunner.doInTransaction(new ITransactionOperation(){
            @Override
            public Void run() {
                for(Integer artifactId:artifactIdIter){
                    artifactResultMapper.updateHatch(artifactId, hatchValue);
                }
                return null;
            }   
        });
    }
    
    public void updateArtifactRound(final Iterable<Integer> artifactIdIter,final Short roundValue){
        txRunner.doInTransaction(new ITransactionOperation(){
            @Override
            public Void run() {
                for(Integer artifactId:artifactIdIter){
                    artifactResultMapper.updateRound(artifactId, roundValue);
                }
                
                return null;
            }   
        });
    }
    
    
}
