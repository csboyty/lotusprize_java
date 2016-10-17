package com.zhongyi.lotusprize.mapper.artifact;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.artifact.Artifact;
import com.zhongyi.lotusprize.domain.artifact.ArtifactIntroduce;

public interface ArtifactMapper {

    int insertArtifact(Artifact artifact);

    int insertArtifactIntroduces(@Param("artifactId") Integer artifactId,
            @Param("introduces") Collection<ArtifactIntroduce> introduces);

    int deleteArtifactIntroduceByArtifactId(Integer artifactId);

    int deleteArtifactResultByArtifactId(Integer artifactId);

    int deleteByArtifactId(Integer artifactId);

    int updateArtifact(Artifact artifact);

    int updateArtifactProfile(@Param("artifactId") Integer artifactId,
            @Param("profile") String profile);

    int updateIntroduceImage(@Param("artifactId") Integer artifactId,
            @Param("pos") Integer pos, @Param("image") String image);
    
    int countArtifactResultScoreBy(@Param("topicId") Integer topicId,
            @Param("title") String title,
            @Param("status") Iterable<Integer> status);

    List<Map<String,Object>> findArtifactResultScoreBy(@Param("topicId") Integer topicId,
            @Param("title") String title,
            @Param("status") Iterable<Integer> status,
            @Param("ordering") String ordering,
            @Param("orderby") String orderby, 
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<Map<String,Object>> findArtifactResultScoreByAccount(Integer accountId);
    
    int updateStatus(@Param(value="artifactIdIter")Iterable<Integer> artifactIdIter,@Param(value="status")Short status);

    
    int countArtifactByVote(@Param("topicId") Integer topicId,
            @Param("title") String title,
            @Param("status") Short status);

    List<Map<String,Object>> findArtifactByVote(@Param("topicId") Integer topicId,
            @Param("title") String title,
            @Param("status") Short status,
            @Param("ordering") String ordering,
            @Param("orderby") String orderby, 
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    Integer countArtifactByTopicAndStatus(@Param("topicId")Integer topicId,@Param("status")Iterable<Integer> status);
    
    
    List<Map<String,Object>> findHonorArtifactBy(@Param("topicId")Integer topicId);
    
    List<Integer> findRound1ArtifactIdByTopicId(Integer topicId);
    
    List<Integer> findRound2ArtifactIdByTopicId(Integer topicId);
    
    List<Artifact> iterArtifact(@Param(value = "baseArtifactId") int baseArtifactId,
            @Param(value = "limit") int limit);
    
}
