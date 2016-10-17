package com.zhongyi.lotusprize.domain.artifact;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhongyi.lotusprize.domain.BaseDomain;

public class ArtifactIntroduce extends BaseDomain {
	
	
	private static final long serialVersionUID = 3205974241716435071L;

	@JsonIgnore
	private Integer artifactId;

	private Integer pos;

	private String image;

	private String text;

	public ArtifactIntroduce() {

	}

	public ArtifactIntroduce(Integer artifactId, Integer pos, String image,
			String text) {
		super();
		this.artifactId = artifactId;
		this.pos = pos;
		this.image = image;
		this.text = text;
	}

	public Integer getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(Integer artifactId) {
		this.artifactId = artifactId;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		return Objects.hash(artifactId,pos);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ArtifactIntroduce))return false;
		ArtifactIntroduce other = (ArtifactIntroduce) o;
		return Objects.equals(artifactId, other.artifactId) 
				&& Objects.equals(pos, other.pos);
	}
	
	

}
