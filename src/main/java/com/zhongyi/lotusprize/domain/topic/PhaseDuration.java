package com.zhongyi.lotusprize.domain.topic;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @Title: PhaseDuration.java
 * @Package com.zhongyi.lotusprize.domain.topic
 * @Description: TODO(添加描述)
 * @author zhongzhenyang at gmail.com
 * @date 2014-3-24 下午12:06:11
 * @version V1.0
 */
public class PhaseDuration implements Serializable {

	private static final long serialVersionUID = -256473529680983895L;

	private Integer topicSettingId;

	private Integer phase;

	private Date startDate;

	private Date endDate;

	public PhaseDuration(Integer topicSettingId, Integer phase) {
		this.topicSettingId = topicSettingId;
		this.phase = phase;
	}

	public PhaseDuration() {

	}

	public Integer getPhase() {
		return phase;
	}

	public void setPhase(Integer phase) {
		this.phase = phase;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof PhaseDuration))
			return false;
		PhaseDuration other = (PhaseDuration) o;
		return Objects.equals(topicSettingId, other.topicSettingId)
				&& Objects.equals(phase, other.phase);
	}

	public int hashCode() {
		return Objects.hash(topicSettingId, phase);
	}
}
