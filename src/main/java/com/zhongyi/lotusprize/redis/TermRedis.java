package com.zhongyi.lotusprize.redis;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.domain.term.Term;
import com.zhongyi.lotusprize.util.Transforms;

@Component
public class TermRedis extends RedisEnable{
	
	public static String name2IdKey(){
		return "lp:term:name2id:hash";
	}
	
	public Integer getTermIdByName(final String name){
		String termId =(String)execute(new RedisOperation(){
			@Override
			public String doWithRedis(Jedis jedis) {
				return jedis.hget(name2IdKey(), name.toLowerCase());
			}
			
		});
		return termId == null ? null :Integer.parseInt(termId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> multiGetTermIdByName(final String[] names){
		final String[] lowcaseNames = new String[names.length];
		for(int i=0;i<names.length;i++){
			lowcaseNames[i] = names[i].toLowerCase();
		}
		List<String> termIdList =(List<String>)execute(new RedisOperation(){

			@Override
			public Object doWithRedis(Jedis jedis) {
				return jedis.hmget(name2IdKey(), lowcaseNames);
			}
			
		});
		return Lists.transform(termIdList, Transforms.stringToIntFunction);
	}
	
	
	public void addTerm(final Term term){
		execute(new RedisOperation(){
			@Override
			public Object doWithRedis(Jedis jedis) {
				return jedis.hset(name2IdKey(), term.getName().toLowerCase(), String.valueOf(term.getId()));
			}
			
			
		});
	}
	
	public void multiAddTerm(final Term[] terms){
		final Map<String,String> term2Id = Maps.newHashMap();
		for(Term term:terms){
			term2Id.put(term.getName().toLowerCase(), String.valueOf(term.getId()));
		}
		execute(new RedisOperation(){
			@Override
			public Void doWithRedis(Jedis jedis) {
				jedis.hmset(name2IdKey(), term2Id);
				return null;
			}
			
		});
	}

}
