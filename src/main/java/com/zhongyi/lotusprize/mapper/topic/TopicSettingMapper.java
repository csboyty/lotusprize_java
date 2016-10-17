package com.zhongyi.lotusprize.mapper.topic;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.topic.PhaseDuration;
import com.zhongyi.lotusprize.domain.topic.TopicSetting;

/**
 * Created by zzy on 14-3-20.
 */
public interface TopicSettingMapper {

    int insertTopicSetting(TopicSetting topicSetting);

    int updateTopicSetting(TopicSetting topicSetting);

    int insertOrUpdatePhaseDuration(@Param("topicSettingId") Integer topicSettingId,
        @Param("phaseDuration") PhaseDuration phaseDuration);

    TopicSetting loadByTopicSettingId(Integer topicSettingId);
}
