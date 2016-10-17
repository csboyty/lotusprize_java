package com.zhongyi.lotusprize.domain.topic;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class Topic extends BaseDomain {

	private static final long serialVersionUID = 4380546853559195547L;

	private Integer id;

	// 选题所有者
	private Integer ownAccountId;

	// 选题类型
	private Short category;

	// 作品数量
	private Integer artifactAmount;

	private String corpLogo;

	private Double reward;

	private String video;

	private String profile;

	private Date createTime;

	private Collection<TopicDetail> topicDetails;

	public Topic() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOwnAccountId() {
		return ownAccountId;
	}

	public void setOwnAccountId(Integer ownAccountId) {
		this.ownAccountId = ownAccountId;
	}

	public Short getCategory() {
		return category;
	}

	public void setCategory(Short category) {
		this.category = category;
	}

	public Integer getArtifactAmount() {
		return artifactAmount;
	}

	public void setArtifactAmount(Integer artifactAmount) {
		this.artifactAmount = artifactAmount;
	}

	public String getCorpLogo() {
		return corpLogo;
	}

	public void setCorpLogo(String corpLogo) {
		this.corpLogo = corpLogo;
	}

	public Double getReward() {
		return reward;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Collection<TopicDetail> getTopicDetails() {
		return topicDetails;
	}

	public void setTopicDetails(Collection<TopicDetail> topicDetails) {
		this.topicDetails = topicDetails;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Topic))
			return false;
		Topic other = (Topic) o;
		return Objects.equals(id, other.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}

}
