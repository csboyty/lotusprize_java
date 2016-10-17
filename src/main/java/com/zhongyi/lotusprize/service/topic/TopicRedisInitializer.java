package com.zhongyi.lotusprize.service.topic;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.mapper.topic.TopicMapper;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.util.JsonUtil;

public class TopicRedisInitializer {

	@Autowired
	private TopicMapper topicMapper;
	
	@Autowired
	private TopicRedis topicRedis;

	@PostConstruct
	public void init() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				TopicIterator topicIt = new TopicIterator(0,20);
				while(topicIt.hasNext()){
					final Topic topic = topicIt.next();
//					final Collection<TopicDetail> topicDetails = topicMapper.findTopicDetailByTopicId(topic.getId());
					topicRedis.execute(new RedisOperation(){
						@Override
						public Void doWithRedis(Jedis jedis) {
							String topicKey = TopicRedis.topicKey(topic.getId());
							Transaction tx = jedis.multi();
							tx.hmset(topicKey, TopicRedis.toStringMap(topic));
							for(TopicDetail topicDetail:topic.getTopicDetails()){
								tx.hset(TopicRedis.topicNamesMap(), TopicRedis.topicNameField(topic.getId(), topicDetail.getLang()), topicDetail.getTitle());
								String topicDetailKey = TopicRedis.topicDetailLangKey(topicDetail.getLang());
								String topicDetailJson = JsonUtil.toJsonString(topicDetail);
								tx.hset(topicKey, topicDetailKey, topicDetailJson);
							}
							tx.exec();
							return null;
						}
						
					});
					
				}
			}
		}).start();
	}

	private class TopicIterator implements Iterator<Topic> {
		
		private final int limit;
		private int offsetTopicId;
		private Iterator<Topic> _iterator;

		private TopicIterator(int offsetTopicId, int limit) {
			this.offsetTopicId = offsetTopicId;
			this.limit = limit;
			_iterator = topicMapper.iterTopic(offsetTopicId, limit).iterator();
		}
		
		

		@Override
		public boolean hasNext() {
			boolean _hasNext = _iterator.hasNext();
			if (!_hasNext) {
				_iterator = topicMapper.iterTopic(offsetTopicId, limit).iterator();
				_hasNext = _iterator.hasNext();
			}
			return _hasNext;
		}

		@Override
		public Topic next() {
			Topic topic = _iterator.next();
			offsetTopicId = topic.getId();
			return topic;
		}

		@Override
		public void remove() {

		}
	}

}
