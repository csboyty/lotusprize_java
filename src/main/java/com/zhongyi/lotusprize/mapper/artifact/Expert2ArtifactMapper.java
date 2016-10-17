package com.zhongyi.lotusprize.mapper.artifact;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface Expert2ArtifactMapper {
    
    
    int insertTopic2Expert(@Param(value="topicId")Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "round") Short round,
            @Param(value = "ratio") Float ratio);
    
    int deleteTopic2Expert(@Param(value="topicId")Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "round") Short round);
    
    List<Integer> findExpertIdByTopicIdAndRound(@Param(value="topicId")Integer topicId,
            @Param(value = "round") Short round);
    
    List<Expert2TopicObject> findExpert2TopicByTopicId(@Param(value="topicId") Integer ...topicId);
    
    Float loadRatioByExpertIdAndTopicIdAndRound(@Param(value = "expertId") Integer expertId,
            @Param(value="topicId")Integer topicId,
            @Param(value = "round") Short round);
    
    int deleteArtifact2ExpertScoreByTopicIdAndExpertIdAndRound(@Param(value="topicId")Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "round") Short round);
    
    
    int deleteArtifact2ExpertScoreByArtifactIdAndRound(@Param(value="artifactId")Integer artifactId,
            @Param(value = "round") Short round);
    

    int insertArtifact2ExpertScore(@Param(value = "artifactId") Integer artifactId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "score") Integer score,
            @Param(value = "weightScore") Float weightScore,
            @Param(value = "round") Short round);


    int countArtifact2ExpertRound1(@Param(value = "topicId") Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "artifactTitle") String artifactTitle,
            @Param(value ="showOption") String showOption);

    List<Map<String, Object>> findArtifact2ExpertRound1(
            @Param(value = "topicId") Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "artifactTitle") String artifactTitle,
            @Param(value ="showOption") String showOption,
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    int countArtifact2ExpertRound2(@Param(value = "topicId") Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "artifactTitle") String artifactTitle,
            @Param(value ="showOption") String showOption);
    
    List<Map<String, Object>> findArtifact2ExpertRound2(
            @Param(value = "topicId") Integer topicId,
            @Param(value = "expertId") Integer expertId,
            @Param(value = "artifactTitle") String artifactTitle,
            @Param(value ="showOption") String showOption,
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    
    List<Map<String, Object>> findArtifact2ExpertByCategoryRound2(@Param(value = "category") Short category,
            @Param(value = "expertId") Integer expertId,
            @Param(value ="showOption") String showOption);
    
    
    int countTopicStatusByExpertAndRound(@Param(value = "expertId") Integer expertId,
            @Param(value = "round") Short round);
    
    List<Map<String,Object>> findTopicStatusByExpertAndRound1(
            @Param(value = "expertId") Integer expertId,
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<Map<String,Object>> findTopicStatusByExpertAndRound2(
            @Param(value = "expertId") Integer expertId,
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<Map<String,Object>> findTopicStatusByExpertAndRound2ByCategory(Integer expertId);
    
    
    List<Map<String,Object>> findScoreByArtifactIdAndRound(@Param(value="artifactId")Integer artifactId,
            @Param(value = "round") Short round);
    
    
    Map<String,Object> loadTopicStatusByExpertAndTopicAndRound1(@Param(value = "expertId") Integer expertId,
            @Param(value = "topicId") Integer topicId);
    
    Map<String,Object> loadTopicStatusByExpertAndTopicAndRound2(@Param(value = "expertId") Integer expertId,
            @Param(value = "topicId") Integer topicId);
    
    
    
    public static class Expert2TopicObject{
        
        private Integer topicId;
        
        private Integer expertId;
        
        private String expertName;
        
        private Short round;
        
        private Float ratio;
        
        private Date createTime;

        public Integer getTopicId() {
            return topicId;
        }

        public void setTopicId(Integer topicId) {
            this.topicId = topicId;
        }

        public Integer getExpertId() {
            return expertId;
        }

        public void setExpertId(Integer expertId) {
            this.expertId = expertId;
        }

        public String getExpertName() {
            return expertName;
        }

        public void setExpertName(String expertName) {
            this.expertName = expertName;
        }

        public Short getRound() {
            return round;
        }

        public void setRound(Short round) {
            this.round = round;
        }

        public Float getRatio() {
            return ratio;
        }

        public void setRatio(Float ratio) {
            this.ratio = ratio;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
        
    }
    

}
