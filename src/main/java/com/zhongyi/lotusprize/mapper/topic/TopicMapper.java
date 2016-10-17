package com.zhongyi.lotusprize.mapper.topic;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.domain.topic.TopicIntroduce;

public interface TopicMapper {

    List<Topic> iterTopic(@Param(value = "baseTopicId") int basePostId,
            @Param(value = "limit") int limit);

    Topic loadByTopicId(@Param("topicId") Integer topicId);

    List<TopicDetail> findTopicDetailByTopicId(Integer topicId);

    int insertTopic(Topic topic);

    int insertTopicDetail(@Param("topicId") Integer topicId,
            @Param("topicDetail") TopicDetail topicDetail);

    int insertTopicIntroduces(@Param("topicId") Integer topicId,
            @Param("lang") String lang,
            @Param("topicIntroduces") Collection<TopicIntroduce> topicIntroduces);

    int countBy(@Param("category") Short category,
            @Param("title") String title, @Param("lang") String lang);

    List<Integer> findTopicIdBy(@Param("category") Short category,
            @Param("title") String title, @Param("lang") String lang,
            @Param("orderby") String orderby,
            @Param("ordering") String ordering,
            @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    
    List<Integer> findTopicIdByAdmin(@Param("category") Short category,
            @Param("title") String title, @Param("lang") String lang,
            @Param("orderby") String orderby,
            @Param("ordering") String ordering,
            @Param("offset") Integer offset, @Param("limit") Integer limit);

    int updateTopic(Topic topic);

    int updateTopicDetail(@Param("topicId") Integer topicId,
            @Param("topicDetail") TopicDetail topicDetail);

    int deleteTopicIntroductionByTopicIdAndLang(
            @Param("topicId") Integer topicId, @Param("lang") String lang);

    int deleteByTopicId(Integer topicId);

    int incrArtifactAmount(@Param("topicId") Integer topicId,
            @Param("incrValue") Integer incrValue);

    int updateVideo(@Param("topicId") Integer topicId,
            @Param("video") String video);

    int updateProfile(@Param("topicId") Integer topicId,
            @Param("profile") String profile);

    int updateCorpLogo(@Param("topicId") Integer topicId,
            @Param("corpLogo") String corpLogo);

    int updateTopicDetailAttachment(@Param("topicId") Integer topicId,
            @Param("lang") String lang, @Param("attachment") String attachment);

    int updateTopicIntroductImage(@Param("topicId") Integer topicId,
            @Param("lang") String lang, @Param("pos") Integer pos,
            @Param("image") String image);

    List<Integer> findTopicIdByAccountIdAndLang(
            @Param(value = "accountId") Integer accountId,
            @Param("lang") String lang);
}
