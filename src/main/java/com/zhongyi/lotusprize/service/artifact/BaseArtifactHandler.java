package com.zhongyi.lotusprize.service.artifact;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.mapper.artifact.ArtifactMapper;
import com.zhongyi.lotusprize.redis.ArtifactRedis;
import com.zhongyi.lotusprize.service.topic.BaseTopicHandler;

public abstract class BaseArtifactHandler extends BaseTopicHandler{
	
	
	@Autowired
	protected ArtifactMapper artifactMapper;

	@Autowired
	protected ArtifactRedis artifactRedis;
	
	
	protected Map<String,Object> createArtifactUpdateSqlParamMap(Integer artifactId,Short round){
		Map<String,Object> updateTotalScoreParamMap = Maps.newHashMap();
		updateTotalScoreParamMap.put("artifactId", artifactId);
		updateTotalScoreParamMap.put("round", round);
		return updateTotalScoreParamMap;
	}


}
