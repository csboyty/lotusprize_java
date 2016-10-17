package com.zhongyi.lotusprize.service.topic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;

@Component
public class TopicRetriveHandler extends BaseTopicHandler{
	
	
	public SqlQueryResult<Map<String,Object>> listBy(Short category,String title,String lang,
			String orderby, String ordering, Integer offset,Integer limit,boolean isAdmin) {
		Integer count = topicMapper.countBy(category, title, lang);
		List<Map<String,Object>> topicList = Collections.emptyList();
		if (count > 0) {
			Collection<Integer> topicIdList;
			if(isAdmin) 
			    topicIdList= topicMapper.findTopicIdByAdmin(category, title, lang,
					orderby, ordering, offset, limit);
			else
			    topicIdList= topicMapper.findTopicIdBy(category, title, lang,
	                    orderby, ordering, offset, limit);
			if (!topicIdList.isEmpty()) {
				topicList = Lists.newArrayListWithCapacity(topicIdList.size());
				for (Integer topicId : topicIdList) {
					topicList.add(getTopicAsMap(topicId,lang,isAdmin));
				}

			}
		}
		return new SqlQueryResult<Map<String,Object>>(count, topicList);
	}
	
	public Collection<Map<String,Object>> listByAccount(Integer ownAccountId,String lang){
		Collection<Integer> topicIdList =topicMapper.findTopicIdByAccountIdAndLang(ownAccountId, lang);
		List<Map<String,Object>> topicList = Collections.emptyList();
		if (!topicIdList.isEmpty()) {
			topicList = Lists.newArrayListWithCapacity(topicIdList.size());
			for (Integer topicId : topicIdList) {
				topicList.add(getTopicAsMap(topicId,lang,true));
			}

		}
		return topicList;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Map<String,Object>> topicId2Names(String userLang){
		Map<String,String> topicNames = (Map<String,String>)topicRedis.execute(new RedisOperation(){

			@Override
			public Map<String,String> doWithRedis(Jedis jedis) {
				return jedis.hgetAll(TopicRedis.topicNamesMap());
			}
			
		});
		List<Map<String,Object>> topicId2Names = Lists.newArrayList();
		for(Map.Entry<String,String> entry:topicNames.entrySet()){
			String lang = entry.getKey().substring(entry.getKey().length()-2);
			if(userLang.equals(lang)){
				Map<String,Object> map = Maps.newHashMap();
				String topicId = entry.getKey().substring(0,entry.getKey().length()-2);
				map.put("id", Integer.parseInt(topicId));
				map.put("name",entry.getValue());
				topicId2Names.add(map);
			}
		}
		return topicId2Names;
	}

}
