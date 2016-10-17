package com.zhongyi.lotusprize.service.artifact;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.mapper.artifact.Expert2ArtifactMapper;
import com.zhongyi.lotusprize.mapper.artifact.Expert2ArtifactMapper.Expert2TopicObject;
import com.zhongyi.lotusprize.redis.RedisEnable.RedisOperation;
import com.zhongyi.lotusprize.redis.TopicRedis;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.ITransactionOperation;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.Transforms;

@Component
public class Artifact2ExpertHandler extends BaseArtifactHandler{
        
    @Autowired
    private Expert2ArtifactMapper expert2ArtifactMapper;
    
    
    public void createTopic2ExpertRound1(final Iterable<Integer> topicIdIt,final Iterable<Integer> expertIdIt){
        final Float topicManagerRatio = ApplicationProperty.instance().getAsFloat("ratio.topic_manager");
        final Float expertRatio = ApplicationProperty.instance().getAsFloat("ratio.expert");
        final short round = 1;
        
        txRunner.doInTransaction(new ITransactionOperation(){
            public Object run() {
                for(Integer topicId:topicIdIt){
                    List<Integer> prevExpertIdList = expert2ArtifactMapper.findExpertIdByTopicIdAndRound(topicId, round);
                    Integer topicManagerId = topicOwnAccountId(topicId);
                    if(! prevExpertIdList.remove(topicManagerId)){
                        expert2ArtifactMapper.insertTopic2Expert(topicId, topicManagerId, round, topicManagerRatio);
                    }
                    
                    for(Integer expertId:expertIdIt){
                        if(! prevExpertIdList.remove(expertId)){
                            expert2ArtifactMapper.insertTopic2Expert(topicId, expertId, round, expertRatio);
                        }
                    }
                    for(Integer prevExpertId:prevExpertIdList){
                        expert2ArtifactMapper.deleteTopic2Expert(topicId, prevExpertId, round);
                        expert2ArtifactMapper.deleteArtifact2ExpertScoreByTopicIdAndExpertIdAndRound(topicId, prevExpertId, round);
                    }
                }
                return null;
            }
        });
    }
    
    public void createTopic2ExpertRound2(final Iterable<Integer> topicIdIt,final Iterable<Integer> expertIdIt){
        final Float expertRatio = 1.0f;
        final short round = 2;
        txRunner.doInTransaction(new ITransactionOperation(){
            public Object run() {
                for(Integer topicId:topicIdIt){
                    List<Integer> prevExpertIdList = expert2ArtifactMapper.findExpertIdByTopicIdAndRound(topicId, round);
                    for(Integer expertId:expertIdIt){
                        if(! prevExpertIdList.remove(expertId)){
                            expert2ArtifactMapper.insertTopic2Expert(topicId, expertId, round, expertRatio);
                        }
                    }
                    for(Integer prevExpertId:prevExpertIdList){
                        expert2ArtifactMapper.deleteTopic2Expert(topicId, prevExpertId, round);
                        expert2ArtifactMapper.deleteArtifact2ExpertScoreByTopicIdAndExpertIdAndRound(topicId, prevExpertId, round);
                    }
                }
                return null;
            }
        });
    }
    
    public SqlQueryResult<Map<String,Object>> listExpertTopicStatus(Integer expertId,Short round,
             Integer offset,Integer limit){
        
        int count = expert2ArtifactMapper.countTopicStatusByExpertAndRound(expertId, round);
        List<Map<String,Object>> expert2TopicList = Collections.emptyList();
        if(count != 0 ){
            if(round ==1 )
                expert2TopicList = expert2ArtifactMapper.findTopicStatusByExpertAndRound1(expertId,  offset, limit);
            else
                expert2TopicList = expert2ArtifactMapper.findTopicStatusByExpertAndRound2(expertId,  offset, limit);
        }
        return new SqlQueryResult<Map<String,Object>>(count,expert2TopicList);
        
    }
    
    
    public List<Map<String,Object>> listExpertTopic(final Integer expertId,final Short round,final String lang){
        List<Map<String,Object>> expert2TopicList;
        if(round==1)
            expert2TopicList= expert2ArtifactMapper.findTopicStatusByExpertAndRound1(expertId,null,null);
        else
            expert2TopicList= expert2ArtifactMapper.findTopicStatusByExpertAndRound2(expertId,null,null);
        
        final List<Map<String,Object>> _expert2TopicList= expert2TopicList;
        if(!expert2TopicList.isEmpty()){
            topicRedis.execute(new RedisOperation(){
                @Override
                public Void doWithRedis(Jedis jedis) {
                    for(Map<String,Object> result:_expert2TopicList){
                        Integer topicId = Transforms.numericToInt(result.get("topicId"));
                        String topicName = jedis.hget(TopicRedis.topicNamesMap(), TopicRedis.topicNameField(topicId, lang));
                        result.put("topicName", topicName);
                        List<String> list = jedis.hmget(TopicRedis.topicKey(topicId), "category","profile");
                        result.put("topicCategory", list.get(0));
                        result.put("topicProfile", list.get(1));
                    }
                    return null;
                }
                
            });
        }
        return expert2TopicList;
    }
    
    
    public List<Map<String,Object>> listExpertTopicRound2ByCategory(final Integer expertId){
        List<Map<String,Object>> expert2TopicByCategoryList = expert2ArtifactMapper.findTopicStatusByExpertAndRound2ByCategory(expertId);
        return expert2TopicByCategoryList;
    }
    
    
    
    
    
    
    
    public SqlQueryResult<Map<String,Object>> listTopic2Expert(Short category,String title,String orderby, 
            String ordering, Integer offset,Integer limit){
        String lang = "zh";
        Integer count = topicMapper.countBy(category, title, lang);
        List<Map<String,Object>> resultList = Collections.emptyList();
        if(count !=0){
            resultList = Lists.newArrayList();
            List<Integer> topicIdList= topicMapper.findTopicIdByAdmin(category, title, lang,
                    orderby, ordering, offset, limit);
            Map<Integer,Map<String,Object>> topic2xpertMap = getTopic2ExpertMap(topicIdList);
            for(Integer topicId:topicIdList){
                Map<String,Object> topic2Expert = Maps.newHashMap();
                topic2Expert.putAll(topicDetail(topicId,lang));
                Map<String,Object> subMap = topic2xpertMap.get(topicId);
                if(subMap == null)
                    subMap = Maps.newHashMap();
                if(!subMap.containsKey("round1"))
                    subMap.put("round1", Collections.emptyList());
                if(!subMap.containsKey("round2"))
                    subMap.put("round2", Collections.emptyList());
                topic2Expert.putAll(subMap);
                resultList.add(topic2Expert);
            }
        }
        return new SqlQueryResult<Map<String,Object>>(count,resultList);
        
    }
    
    public List<Map<String,Object>> artifactScores(Integer artifactId,Short round){
        return expert2ArtifactMapper.findScoreByArtifactIdAndRound(artifactId, round);
    }
    
    
    public Map<String,Object> getExpertTopicRoundStatus(Integer expertId,Integer topicId,short round){
        Map<String,Object> expertTopicRoundtatus;
        if(round ==1)
            expertTopicRoundtatus = expert2ArtifactMapper.loadTopicStatusByExpertAndTopicAndRound1(expertId, topicId);
        else
            expertTopicRoundtatus = expert2ArtifactMapper.loadTopicStatusByExpertAndTopicAndRound2(expertId, topicId);
        return expertTopicRoundtatus;
    }
    
    
    private Map<Integer,Map<String,Object>> getTopic2ExpertMap(List<Integer> topicIdList){
        List<Expert2TopicObject> expert2TopicObjectList = expert2ArtifactMapper.findExpert2TopicByTopicId(topicIdList.toArray(new Integer[0]));
        String roundBase ="round";
        Float topicManagerRatio = ApplicationProperty.instance().getAsFloat("ratio.topic_manager");
        Map<Integer,Map<String,Object>> map = Maps.newHashMap();
        for(int i=0;i<expert2TopicObjectList.size();i++){
            Expert2TopicObject object = expert2TopicObjectList.get(i);
            Integer topicId = object.getTopicId();
            Map<String,Object> subMap = map.get(topicId);
            if(subMap == null){
                subMap = Maps.newHashMap();
                map.put(topicId, subMap);
            }
            String roundKey = roundBase + object.getRound();
            List<Map<String,Object>> expertList = (List<Map<String,Object>>)subMap.get(roundKey);
            if(expertList == null){
                expertList = Lists.newArrayList();
            }
            if(!topicManagerRatio.equals(object.getRatio())){
                Map<String,Object> expertInfo = Maps.newHashMap();
                expertInfo.put("expertId", object.getExpertId());
                expertInfo.put("expertName", object.getExpertName());
                expertList.add(expertInfo);
            }
            subMap.put(roundKey, expertList);
        }
        return map;
        
    }
    
    
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> topicDetail(final Integer topicId,final String lang){
        return (Map<String,Object>)topicRedis.execute(new RedisOperation(){
            @Override
            public  Map<String,Object> doWithRedis(Jedis jedis) {
                Map<String,Object> topic = Maps.newHashMap();
                String topicKey = TopicRedis.topicKey(topicId);
                List<String> values =jedis.hmget(topicKey, "category","artifactAmount","reward",TopicRedis.topicDetailLangKey(lang));
                topic.put("id", topicId);
                topic.put("category",Short.parseShort(values.get(0)));
                topic.put("artifactAmount", Integer.parseInt(values.get(1)));
                topic.put("reward",values.get(2));
                String topicDetailJson = values.get(3);
                if(topicDetailJson!=null){
                    TopicDetail topicDetail = JsonUtil.fromJson(TopicRedis.topicDetailType, topicDetailJson);
                    topic.put("corpName",topicDetail.getCorp());
                    topic.put("name", topicDetail.getTitle());
                }
                return topic;
            }
            
        });
    }
    

}
