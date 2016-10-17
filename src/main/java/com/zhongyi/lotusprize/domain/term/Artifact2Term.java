package com.zhongyi.lotusprize.domain.term;

import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class Artifact2Term extends BaseDomain {

	private static final long serialVersionUID = 2848398003211271477L;

	private Integer _artifactId;

	private Integer _termId;

	private Integer _termAttachAccountId;

	private Date _termAttachTime;

	public Artifact2Term() {
		super();
	}

	public Artifact2Term(Integer artifactId, Integer termId,
			Integer termAttachAccountId, Date termAttachTime) {
		super();
		this._artifactId = artifactId;
		this._termId = termId;
		this._termAttachAccountId = termAttachAccountId;
		this._termAttachTime = termAttachTime;
	}

	public Integer getArtifactId() {
		return _artifactId;
	}

	public void setArtifactId(Integer artifactId) {
		this._artifactId = artifactId;
	}

	public Integer getTermId() {
		return _termId;
	}

	public void setTermId(Integer termId) {
		this._termId = termId;
	}

	public Integer getTermAttachAccountId() {
		return _termAttachAccountId;
	}

	public void setTermAttachAccountId(Integer termAttachAccountId) {
		this._termAttachAccountId = termAttachAccountId;
	}

	public Date getTermAttachTime() {
		return _termAttachTime;
	}

	public void setTermAttachTime(Date termAttachTime) {
		this._termAttachTime = termAttachTime;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Artifact2Term))
			return false;
		Artifact2Term other = (Artifact2Term) o;
		return Objects.equals(_artifactId, other._artifactId)
				&& Objects.equals(_termId, other._termId);
	}

	public int hashCode() {
		return Objects.hash(_artifactId, _termId);
	}

}
