package com.zhongyi.lotusprize.domain.topic;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class TopicSetting extends BaseDomain {


    private static final long serialVersionUID = 527957619300362254L;

    private Integer id;

    private String name;

    private Integer currentPhase;

    private Boolean active;

    private Collection<PhaseDuration> phaseDurations;

    private Date createTime;

    private Date updateTime;

    public TopicSetting() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Integer currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Collection<PhaseDuration> getPhaseDurations() {
        return phaseDurations;
    }

    public void setPhaseDurations(Collection<PhaseDuration> phaseDurations) {
        this.phaseDurations = phaseDurations;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TopicSetting)) return false;
        TopicSetting other = (TopicSetting) o;
        return Objects.equals(id, other.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }


  
}
