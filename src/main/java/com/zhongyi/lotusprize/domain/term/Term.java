package com.zhongyi.lotusprize.domain.term;

import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class Term extends BaseDomain {

	private static final long serialVersionUID = -3580172503731162072L;

	private Integer _id;

	private String _name;

	private Date _createTime;

	public Term() {
		super();
	}

	public Term(String name) {
		super();
		this._name = name;
	}

	
	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public Date getCreateTime() {
		return _createTime;
	}

	public void setCreateTime(Date createTime) {
		this._createTime = createTime;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Term))
			return false;
		Term other = (Term) o;
		return Objects.equals(_name, other._name);
	}

	public int hashCode() {
		return Objects.hash(_name);
	}

}
