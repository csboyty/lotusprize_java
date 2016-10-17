package com.zhongyi.lotusprize.domain.email;

import java.io.Serializable;

public class EmailBody implements Serializable {

	private static final long serialVersionUID = -2392107188720814520L;

	private String _subject;

	private String _text;

	private String _attachment;

	public EmailBody() {
		super();
	}

	public EmailBody(String subject, String text, String attachment) {
		super();
		this._subject = subject;
		this._text = text;
		this._attachment = attachment;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		this._text = text;
	}

	public String getSubject() {
		return _subject;
	}

	public void setSubject(String subject) {
		this._subject = subject;
	}

	public String getAttachment() {
		return _attachment;
	}

	public void setAttachment(String attachment) {
		this._attachment = attachment;
	}

}
