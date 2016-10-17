package com.zhongyi.lotusprize.service.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.util.DateTimeUtil;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.Transforms;

@Component
public class ArtifactRetriveHandler extends BaseArtifactHandler{
	
	public Collection<Map<String,Object>> userArtifacts(final Integer accountId,final String lang){
	    final List<Map<String,Object>> artifactResultList = artifactMapper.findArtifactResultScoreByAccount(accountId);
		if(!artifactResultList.isEmpty()){
    	    artifactRedis.execute(new RedisOperation(){
    			@Override
    			public Void doWithRedis(Jedis jedis) {
    				for(Map<String,Object> artifactResult:artifactResultList){
    				    Integer artifactId = Transforms.numericToInt(artifactResult.get("id"));
    					Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
    					Map<String,Object> artifactMap = toAdminViewMap(map);
    					artifactResult.putAll(artifactMap);
    					String topicName = jedis.hget(TopicRedis.topicNamesMap(), TopicRedis.topicNameField((Integer)artifactMap.get("topicId"), lang));
    					artifactResult.put("topicName", topicName);
    				}
    				return null;
    			}
    		});
		}
		return artifactResultList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> artifactById(final Integer artifactId,final String lang){
		return (Map<String,Object>)artifactRedis.execute(new RedisOperation(){
			public Map<String,Object> doWithRedis(Jedis jedis) {
				Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
				Map<String,Object> artifactMap = toAdminViewMap(map);
				String topicName = jedis.hget(TopicRedis.topicNamesMap(), TopicRedis.topicNameField((Integer)artifactMap.get("topicId"), lang));
				artifactMap.put("topicName", topicName);
				return artifactMap;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<Map<String,Object>> artifactIntroduceById(final Integer artifactId){
		return  (Collection<Map<String,Object>>)artifactRedis.execute(new RedisOperation(){
			@Override
			public Object doWithRedis(Jedis jedis) {
				String introducesJson = jedis.hget(ArtifactRedis.artifactKey(artifactId), "introduces");
				if(!Strings.isNullOrEmpty(introducesJson))
					return JsonUtil.fromJson(new TypeReference<Collection<Map<String,Object>>>(){},introducesJson);
				return Collections.emptyList();
			}
			
		});
		
	}
	
	public SqlQueryResult<Map<String,Object>> listByAdminView(Integer topicId,String title,Iterable<Integer> status,
			String orderby,	String ordering,Integer offset,Integer limit){
		
		Integer count = artifactMapper.countArtifactResultScoreBy(topicId, title, status);
		final List<Map<String,Object>> artifactList = Lists.newArrayListWithCapacity(10);
		if(count != 0){
			final List<Map<String,Object>> artifactScoreResultList = artifactMapper.findArtifactResultScoreBy(topicId, title, 
			        status,  ordering, orderby, offset, limit);
			if(!artifactScoreResultList.isEmpty()){
				artifactRedis.execute(new RedisOperation(){
					@Override
					public Void doWithRedis(Jedis jedis) {
						for(Map<String,Object> artifactScoreResult:artifactScoreResultList){
						    pretendArtifactScoreResult(artifactScoreResult);
						    Integer artifactId = Transforms.numericToInt(artifactScoreResult.get("id"));
							Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
							Map<String,Object> artifactMap = toAdminViewMap(map);
							artifactScoreResult.putAll(artifactMap);
							artifactList.add(artifactScoreResult);
						}
						return null;
					}
					
				});
			}
		}
		return new SqlQueryResult<Map<String,Object>>(count,artifactList);
	}
	
	
	private void pretendArtifactScoreResult(Map<String,Object> artifactScoreResult){
	    if(!artifactScoreResult.containsKey("totalPraise")){
	        artifactScoreResult.put("totalPraise", 0);
	    }
	    if(!artifactScoreResult.containsKey("prize")){
	        artifactScoreResult.put("prize", 0);
	    }
	    if(!artifactScoreResult.containsKey("hatch")){
	        artifactScoreResult.put("hatch", 0);
	    }
	    
	    if(!artifactScoreResult.containsKey("round")){
	        artifactScoreResult.put("round", 0);
	    }
	}
	
	
	public SqlQueryResult<Map<String,Object>> listByAnonView(Integer topicId,String title,Short status,
            String orderby, String ordering,Integer offset,Integer limit){
        Integer count = artifactMapper.countArtifactByVote(topicId, title, status);
        final List<Map<String,Object>> artifactList = Lists.newArrayListWithCapacity(10);
        if(count != 0){
            final List<Map<String,Object>> artifactScoreResultList = artifactMapper.findArtifactByVote(topicId, title, 
                    status, ordering, orderby, offset, limit);
            if(!artifactScoreResultList.isEmpty()){
                artifactRedis.execute(new RedisOperation(){
                    @Override
                    public Void doWithRedis(Jedis jedis) {
                        for(Map<String,Object> artifactScoreResult:artifactScoreResultList){
                            Integer artifactId = Transforms.numericToInt(artifactScoreResult.get("id"));
                            Map<String,String> map = jedis.hgetAll(ArtifactRedis.artifactKey(artifactId));
                            Map<String,Object> artifactMap = toAdminViewMap(map);
                            artifactMap.remove("status");
                            artifactScoreResult.putAll(artifactMap);
                            artifactList.add(artifactScoreResult);
                        }
                        return null;
                    }
                });
            }
        }
        return new SqlQueryResult<Map<String,Object>>(count,artifactList);
    }
	
	
	public int countArtifactByTopicAndStatus(Integer topicId,Iterable<Integer> status){
	    return artifactMapper.countArtifactByTopicAndStatus(topicId, status);
	}
	
	
	private Map<String,Object> toAdminViewMap(Map<String,String> redisMap){
		Map<String,Object> artifactMap = new HashMap<String,Object>();
		artifactMap.put("id", Integer.parseInt(redisMap.get("id")));
		artifactMap.put("topicId", Integer.parseInt(redisMap.get("topicId")));
		artifactMap.put("ownAccountId", Integer.parseInt(redisMap.get("ownAccountId")));
		artifactMap.put("title", redisMap.get("title"));
		artifactMap.put("description", redisMap.get("description"));
		artifactMap.put("profile", redisMap.get("profile"));
		artifactMap.put("organization", redisMap.get("organizations"));
		artifactMap.put("author",redisMap.get("authors"));
		artifactMap.put("createTime", DateTimeUtil.formatAsYYYYMMddHHmmss(new Date(Long.parseLong(redisMap.get("createTime")))));
		artifactMap.put("introduces", JsonUtil.fromJson(new TypeReference<List<Map<String,Object>>>(){},redisMap.get("introduces")));
		artifactMap.put("status",Short.parseShort(redisMap.get("status")));
		artifactMap.put("attachment", redisMap.get("attachment"));
		return artifactMap;
	}
	
	public List<Map<String,Object>> topicHonorArtifact(Integer topicId){
	    return artifactMapper.findHonorArtifactBy(topicId);
	}
	
	public List<Map<String,Object>> honorArtifact(){
	    return artifactMapper.findHonorArtifactBy(null);
	}
	
	

}
