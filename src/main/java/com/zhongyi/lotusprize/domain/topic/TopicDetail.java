package com.zhongyi.lotusprize.domain.topic;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Title: TopicDetail.java
 * @Package com.zhongyi.lotusprize.domain.topic
 * @Description: TODO(添加描述)
 * @author zhongzhenyang at gmail.com
 * @date 2014-3-24 下午12:04:42
 * @version V1.0
 */
public class TopicDetail implements Serializable {

	private static final long serialVersionUID = 3338493250274701860L;
	
	@JsonIgnore
	private Integer topicId;
	
	// 语言
	private String lang;
	
	@JsonIgnore
	private Integer topicSettingId;

	// 选题名称
	private String title;
	
	
	private String description;

	// 公司
	private String corp;

	// 额外奖励
	private String addition;

	// 附件
	private String attachment;

	// 选题介绍
	private Collection<TopicIntroduce> topicIntroduces;

	public TopicDetail() {

	}

	public TopicDetail(Integer topicId, String lang) {
		super();
		this.topicId = topicId;
		this.lang = lang;
	}
	
	public Integer getTopicId() {
		return topicId;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	@JsonProperty("name")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("corpName")
	public String getCorp() {
		return corp;
	}

	public void setCorp(String corp) {
		this.corp = corp;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Collection<TopicIntroduce> getTopicIntroduces() {
		return topicIntroduces;
	}

	public void setTopicIntroduces(
			Collection<TopicIntroduce> topicIntroduces) {
		this.topicIntroduces = topicIntroduces;
	}

	public Integer getTopicSettingId() {
		return topicSettingId;
	}

	public void setTopicSettingId(Integer topicSettingId) {
		this.topicSettingId = topicSettingId;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	
	public boolean equals(Object o){
		if(o == this)return true;
		if(!(o instanceof TopicDetail))return false;
		TopicDetail other = (TopicDetail)o;
		return Objects.equals(topicId, other.topicId) && Objects.equals(lang, other.lang);
	}
	
	public int hashCode(){
		return Objects.hash(topicId,lang);
	}

}

