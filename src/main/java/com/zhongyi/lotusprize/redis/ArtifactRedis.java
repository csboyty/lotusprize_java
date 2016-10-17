package com.zhongyi.lotusprize.redis;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhongyi.lotusprize.domain.artifact.Artifact;
import com.zhongyi.lotusprize.domain.artifact.ArtifactIntroduce;
import com.zhongyi.lotusprize.util.JsonUtil;

@Component
public class ArtifactRedis extends RedisEnable{
	

	
	public static String artifactKey(Integer artifactId){
		return artifactKey(String.valueOf(artifactId));
	}
	
	public static String artifactKey(String artifactId){
		return "lp:artifact-hash:"+artifactId;
	}
	
	public static String artifactVoteKey(Integer artifactId){
	    return "lp:artifact-vote-set:"+artifactId;
	}
	

	
	public Artifact getArtifact(final Integer artifactId){
		return (Artifact)execute(new RedisOperation(){
			@Override
			public Artifact doWithRedis(Jedis jedis) {
				String artifactKey = ArtifactRedis.artifactKey(artifactId);
				Map<String,String> map = jedis.hgetAll(artifactKey);
				if(map != null && !map.isEmpty()){
					return asArtifact(map);
				}
				return null;
			}
			
		});
	}
	
	
	public static Artifact asArtifact(Map<String,String> map){
		Artifact artifact = new Artifact();
		artifact.setId(Integer.parseInt(map.get("id")));
		artifact.setTopicId(Integer.parseInt(map.get("topicId")));
		artifact.setOwnAccountId(Integer.parseInt(map.get("ownAccountId")));
		artifact.setTitle(map.get("title"));
		artifact.setDescription(map.get("description"));
		artifact.setProfile(map.get("profile"));
		artifact.setOrganizations(map.get("organizations"));
		artifact.setAuthors(map.get("authors"));
		artifact.setCreateTime(new Date(Long.parseLong(map.get("createTime"))));
		artifact.setStatus(Short.parseShort(map.get("status")));
		artifact.setAttachment(map.get("attachment"));
		if(map.get("introduces")!= null){
			artifact.setIntroduces(JsonUtil.fromJson(new TypeReference<Collection<ArtifactIntroduce>>(){},map.get("introduces")));
		}
		return artifact;
	}
	
	public static Map<String,String> toStringMap(Artifact artifact){
		Map<String,String> map = new HashMap<String,String>();
		map.put("id", String.valueOf(artifact.getId()));
		map.put("topicId", String.valueOf(artifact.getTopicId()));
		map.put("ownAccountId", String.valueOf(artifact.getOwnAccountId()));
		map.put("title", artifact.getTitle());
		map.put("description", artifact.getDescription());
		map.put("profile", artifact.getProfile());
		map.put("organizations", artifact.getOrganizations());
		map.put("authors", artifact.getAuthors());
		map.put("attachment", artifact.getAttachment());
		if(artifact.getStatus() != null)
		    map.put("status", String.valueOf(artifact.getStatus()));
		map.put("createTime", String.valueOf(artifact.getCreateTime().getTime()));
		if(artifact.getIntroduces()!=null){
			map.put("introduces", JsonUtil.toJsonString(artifact.getIntroduces()));
		}
		
		return map;
	}
	
	public Integer artifactTopicId(final Integer artifactId){
		String topicIdString = (String)execute(new RedisOperation(){

			@Override
			public String doWithRedis(Jedis jedis) {
				return jedis.hget(ArtifactRedis.artifactKey(artifactId), "topicId");
			}
			
		});
		return Integer.parseInt(topicIdString);
	}
	
}
