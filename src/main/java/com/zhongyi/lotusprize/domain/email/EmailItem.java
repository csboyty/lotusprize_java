package com.zhongyi.lotusprize.domain.email;

import java.util.Date;
import java.util.Objects;

import com.zhongyi.lotusprize.domain.BaseDomain;

public class EmailItem extends BaseDomain {

	private static final long serialVersionUID = 492147950810849317L;

	public enum Status {
		waiting, sent, fail
	}

	public enum Type {
		confirm, reset_password,score
	}

	private Integer _id;

	private String _address;

	private EmailBody _body;

	private Status _status;

	private Type _type;

	private Date _createTime;

	private Date _sendTime;

	private String _serverId;

	public EmailItem() {

	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		this._address = address;
	}

	public EmailBody getBody() {
		return _body;
	}

	public void setBody(EmailBody body) {
		this._body = body;
	}

	public Status getStatus() {
		return _status;
	}

	public void setStatus(Status status) {
		this._status = status;
	}

	public Type getType() {
		return _type;
	}

	public void setType(Type type) {
		this._type = type;
	}

	public Date getCreateTime() {
		return _createTime;
	}

	public void setCreateTime(Date createTime) {
		this._createTime = createTime;
	}

	public Date getSendTime() {
		return _sendTime;
	}

	public void setSendTime(Date sendTime) {
		this._sendTime = sendTime;
	}

	public String getServerId() {
		return _serverId;
	}

	public void setServerId(String serverId) {
		this._serverId = serverId;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof EmailItem))
			return false;
		EmailItem other = (EmailItem) o;
		return Objects.equals(_id, other._id);
	}

	public int hashCode() {
		return Objects.hash(_id);
	}

}
