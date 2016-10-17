package com.zhongyi.lotusprize.domain.topic;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Title: TopicIntroduce.java
 * @Package com.zhongyi.lotusprize.domain.topic
 * @Description: TODO(添加描述)
 * @author zhongzhenyang at gmail.com
 * @date 2014-3-24 下午12:05:30
 * @version V1.0
 */
public  class TopicIntroduce implements Serializable {

	private static final long serialVersionUID = 3692720720296514924L;
	
	@JsonIgnore
	private Integer topicId;
	
	@JsonIgnore
	private String lang;

	private Integer pos;
	
	private String text;

	private String image;

	public TopicIntroduce(Integer topicId, String lang) {
		this.topicId = topicId;
		this.lang = lang;
	}

	public TopicIntroduce() {

	}
	

	public Integer getTopicId() {
		return topicId;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public boolean equals(Object o){
		if(o == this)return true;
		if(!(o instanceof TopicIntroduce))return false;
		TopicIntroduce other = (TopicIntroduce)o;
		return Objects.equals(topicId, other.topicId)
			&& Objects.equals(lang,other.lang)
			&& Objects.equals(pos, other.pos);
	}
	
	public int hashCode(){
		return Objects.hash(topicId,lang,pos);
	}
	
	
}

