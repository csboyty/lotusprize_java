package com.zhongyi.lotusprize.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.util.JsonUtil;

@Component
public class TopicRedis extends RedisEnable{
	
	public static final String lang_prefix = "i18n:";
	
	public static final TypeReference<TopicDetail> topicDetailType = new TypeReference<TopicDetail>(){};
	
	
	public static String stageSettingKey(){
	    return "lp:stage-setting";
	}
	
	public static String topicKey(Integer topicId){
		return String.format("lp:topic-hash:%d", topicId);
	}
	
	public static String topicDetailLangKey(String lang){
		return lang_prefix+lang;
	}
	
	public static String topicNamesMap(){
		return "lp:topic-name-hash";
	}
	
	public static String topicNameField(Integer topicId,String lang){
		return topicId+lang;
	}
	
	
	
	public static Map<String,String> toStringMap(Topic topic){
		Map<String,String> map = Maps.newHashMap();
		map.put("id", String.valueOf(topic.getId()));
		map.put("ownAccountId", String.valueOf(topic.getOwnAccountId()));
		map.put("category", String.valueOf(topic.getCategory()));
		map.put("corpLogo", topic.getCorpLogo());
		map.put("reward", String.valueOf(topic.getReward()));
		if(topic.getVideo()!= null)
			map.put("video", topic.getVideo());
		if(topic.getProfile() != null)
			map.put("profile",topic.getProfile());
		if(topic.getArtifactAmount() !=null)
			map.put("artifactAmount",String.valueOf(topic.getArtifactAmount()));
		if(topic.getCreateTime()!=null)
			map.put("createTime", String.valueOf(topic.getCreateTime().getTime()));	
		return map;
	}
	
	public static Topic asTopicWithDetail(Integer topicId,String lang,Jedis jedis){
		String topicKey = topicKey(topicId);
		List<String> values =jedis.hmget(topicKey, "id","ownAccountId","category","artifactAmount","corpLogo","reward","video","profile","createTime",topicDetailLangKey(lang));
		Topic topic = new Topic();
		topic.setId(Integer.parseInt(values.get(0)));
		topic.setOwnAccountId(Integer.parseInt(values.get(1)));
		topic.setCategory(Short.parseShort(values.get(2)));
		topic.setArtifactAmount(values.get(3) != null ? Integer.parseInt(values.get(3)):0);
		topic.setCorpLogo(values.get(4));
		topic.setReward(Double.parseDouble(values.get(5)));
		topic.setVideo(values.get(6));
		topic.setProfile(values.get(7));
		topic.setCreateTime(new Date(Long.parseLong(values.get(8))));
		String topicDetailJson = values.get(9);
		if(topicDetailJson!=null){
			TopicDetail topicDetail = JsonUtil.fromJson(topicDetailType, topicDetailJson);
			topic.setTopicDetails(Lists.newArrayList(topicDetail));
		}
		return topic;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
