package com.zhongyi.lotusprize.service.topic;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.mapper.topic.TopicMapper;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.service.ITransactionRunner;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.util.JsonUtil;

public abstract class BaseTopicHandler {
	
	@Autowired
	protected TopicMapper topicMapper;

	@Autowired
	protected ITransactionRunner txRunner;
	
	@Autowired
	protected TopicRedis topicRedis;
	
	public Integer topicOwnAccountId(final Integer topicId){
	    return (Integer)topicRedis.execute(new RedisOperation(){

            @Override
            public Integer doWithRedis(Jedis jedis) {
                String topicKey = TopicRedis.topicKey(topicId);
                return Ints.tryParse(jedis.hget(topicKey, "ownAccountId"));
            }
	        
	    });
	}

	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getTopicAsMap(final Integer topicId,final String lang,final boolean isAdmin) {
		
	    
	    
	    return (Map<String,Object>)topicRedis.execute(new RedisOperation(){
			@Override
			public Map<String,Object> doWithRedis(Jedis jedis) {
				String topicKey = TopicRedis.topicKey(topicId);
				List<String> values =jedis.hmget(topicKey, "id","ownAccountId","category","artifactAmount","corpLogo","reward","video","profile","createTime",TopicRedis.topicDetailLangKey(lang));
				if(values.get(0) == null){
					return null;
				}
				Map<String,Object> map = Maps.newHashMap();
				map.put("id", Integer.parseInt(values.get(0)));
				map.put("ownAccountId", Integer.parseInt(values.get(1)));
				map.put("category", Short.parseShort(values.get(2)));
				if(isAdmin)
				    map.put("artifactAmount", values.get(3) != null ? Integer.parseInt(values.get(3)):0);
				map.put("corpLogo", values.get(4));
				map.put("reward", (int)Double.parseDouble(values.get(5)));
				map.put("video",values.get(6));
				map.put("profile", values.get(7));
				map.put("createTime", new Date(Long.parseLong(values.get(8))));
				String topicDetailJson = values.get(9);
				if(topicDetailJson!=null){
					map.putAll(JsonUtil.fromJson(topicDetailJson));
				}
				if(isAdmin){
					Collection<String> keys = jedis.hkeys(topicKey);
					Map<String,Boolean> allLang = Maps.newHashMap();
					for(String key:keys){
						int langPos = key.indexOf(TopicRedis.lang_prefix);
						if(langPos==0){
							allLang.put(key.substring(TopicRedis.lang_prefix.length()), true);
						}
					}
					map.put("allLang", allLang);
				}
				return map;
			}
			
		});
		
	}
	
	protected String toLocalHttpFile(String oldHttpUri, String newHttpUri,	Integer userId) throws IOException {
		if (!Strings.isNullOrEmpty(newHttpUri)) {
			if (!newHttpUri.equals(oldHttpUri)) {
				if (LotusprizeLocalFiles.instance().isTempFile(newHttpUri)) {
					return LotusprizeLocalFiles.instance()
							.tempHttpPathToLocalHttpPath(newHttpUri, userId);
				}
			}
		}
		return newHttpUri;
	}
	
	public String getNameByTopicId(final Integer topicId,final String lang){
		return (String)topicRedis.execute(new RedisOperation(){
			@Override
			public String doWithRedis(Jedis jedis) {
				return jedis.hget(TopicRedis.topicNamesMap(), TopicRedis.topicNameField(topicId, lang));
			}
		});
	}
	
	protected Topic getTopicDetail(final Integer topicId,final String lang){
		if(topicId != null){
				return (Topic)topicRedis.execute(new RedisOperation(){
				@Override
				public Topic doWithRedis(Jedis jedis) {
					return TopicRedis.asTopicWithDetail(topicId, lang, jedis);
				}
			});
		}
		return null;
	}
	

	
}
