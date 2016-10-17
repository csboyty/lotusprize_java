package com.zhongyi.lotusprize.service.artifact;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.mapper.artifact.Expert2ArtifactMapper;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.Transforms;

@Component
public class ArtifactExpertOperationHandler extends BaseArtifactHandler{
	
	@Autowired
	private Expert2ArtifactMapper expert2ArtifactMapper;
	
	
	
	public SqlQueryResult<Map<String,Object>> listByTopic(final Integer expertId,final Integer topicId,
	        final Short round,
	        final String artifactTitle,
	        final String showOption,
	        final Integer offset,final Integer limit){
	    int count =0;
	    List<Map<String,Object>> artifact2ScoreList = null ;
	    if(round ==1){
	        count = expert2ArtifactMapper.countArtifact2ExpertRound1(topicId, expertId, artifactTitle,showOption);
	        if(count != 0)
	            artifact2ScoreList = expert2ArtifactMapper.findArtifact2ExpertRound1(topicId, expertId, artifactTitle,showOption, offset, limit);
	    }else if(round ==2){
	        count = expert2ArtifactMapper.countArtifact2ExpertRound2(topicId, expertId, artifactTitle,showOption);
	        if(count != 0)
	            artifact2ScoreList = expert2ArtifactMapper.findArtifact2ExpertRound2(topicId, expertId, artifactTitle,showOption, offset, limit);
	    }
	    final List<Map<String,Object>> searchList = artifact2ScoreList;
	    final List<Map<String,Object>> resultList =Lists.newArrayListWithCapacity(10);
        if(artifact2ScoreList!= null && !artifact2ScoreList.isEmpty()){
            artifactRedis.execute(new RedisOperation(){
                @Override
                public Void doWithRedis(Jedis jedis) {
                    for(Map<String,Object> temp:searchList){
                        Integer artifactId = Transforms.numericToInt(temp.get("artifactId"));
                        Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
                        Map<String,Object> expertViewMap = toExpertViewMap(map);
                        expertViewMap.put("score", temp.get("score"));
                        resultList.add(expertViewMap);
                    }
                    return null;
                }
                
            });
        }
		return new SqlQueryResult<Map<String,Object>>(count,resultList);
	}
	
	public SqlQueryResult<Map<String,Object>> listByTopicByCategoryRound2(final Short category,final Integer expertId, final String showOption){
	    List<Map<String,Object>> artifact2ScoreList =  expert2ArtifactMapper.findArtifact2ExpertByCategoryRound2(category, expertId, showOption);
	    final List<Map<String,Object>> searchList = artifact2ScoreList;
        final List<Map<String,Object>> resultList =Lists.newArrayListWithCapacity(artifact2ScoreList.size());
        if(artifact2ScoreList!= null && !artifact2ScoreList.isEmpty()){
            artifactRedis.execute(new RedisOperation(){
                @Override
                public Void doWithRedis(Jedis jedis) {
                    for(Map<String,Object> temp:searchList){
                        Integer artifactId = Transforms.numericToInt(temp.get("artifactId"));
                        Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
                        Map<String,Object> expertViewMap = toExpertViewMap(map);
                        expertViewMap.put("score", temp.get("score"));
                        resultList.add(expertViewMap);
                    }
                    return null;
                }
                
            });
        }
        return new SqlQueryResult<Map<String,Object>>(resultList.size(),resultList);
	}
	
	
	
	
	
	
	
	public void doArtifactScore(final Integer expertId,final Integer artifactId,final Short round,final Integer score){
	    Integer topicId = artifactRedis.artifactTopicId(artifactId);
	    Float ratio = expert2ArtifactMapper.loadRatioByExpertIdAndTopicIdAndRound(expertId, topicId, round);
	    if(ratio != null){
	        final float weightScore = ratio * score;
	        expert2ArtifactMapper.insertArtifact2ExpertScore(artifactId, expertId, score, weightScore, round);
	    }
	}
	
	
	private Map<String,Object> toExpertViewMap(Map<String,String> redisMap){
		Map<String,Object> artifactMap = new HashMap<String,Object>();
		artifactMap.put("id", Integer.parseInt(redisMap.get("id")));
		artifactMap.put("topicId", Integer.parseInt(redisMap.get("topicId")));
		artifactMap.put("ownAccountId", Integer.parseInt(redisMap.get("ownAccountId")));
		artifactMap.put("title", redisMap.get("title"));
		artifactMap.put("description", redisMap.get("description"));
		artifactMap.put("profile", redisMap.get("profile"));
		artifactMap.put("organization", redisMap.get("organizations"));
		artifactMap.put("author",redisMap.get("authors"));
		artifactMap.put("introduces", JsonUtil.fromJson(new TypeReference<Collection<Map<String,Object>>>(){},redisMap.get("introduces")));
		return artifactMap;
	}
	

}
