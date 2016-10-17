package com.zhongyi.lotusprize.domain.artifact;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class Artifact extends BaseDomain {
	
	private static final long serialVersionUID = 2205741939240874806L;

	private Integer id;

	private Integer topicId;

	private Integer ownAccountId;

	private String title;

	private String description;

	private String profile;
	
	private Date createTime;
	
	private String organizations;

	private String authors;
	
	private Short status;
	
	private String attachment;
	
	private Collection<ArtifactIntroduce> introduces;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTopicId() {
		return topicId;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	public Integer getOwnAccountId() {
		return ownAccountId;
	}

	public void setOwnAccountId(Integer ownAccountId) {
		this.ownAccountId = ownAccountId;
	}

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

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	public String getOrganizations() {
		return organizations;
	}

	public void setOrganizations(String organizations) {
		this.organizations = organizations;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    
	public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Collection<ArtifactIntroduce> getIntroduces() {
		return introduces;
	}

	public void setIntroduces(Collection<ArtifactIntroduce> introduces) {
		this.introduces = introduces;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Artifact))
			return false;
		Artifact other = (Artifact) o;
		return Objects.equals(id, other.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}

}
