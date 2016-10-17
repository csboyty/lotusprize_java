package com.zhongyi.lotusprize.email;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zhongyi.lotusprize.domain.email.EmailItem;
import com.zhongyi.lotusprize.service.ApplicationProperty;

/**
 * @author zhongzhenyang at gmail.com
 * @version V1.0
 * @Title: VelocityMimeMessage.java
 * @Package zhongyi.dev2.email
 * @Description: TODO(添加描述)
 * @date 2013-9-24 上午9:40:53
 */
class LotusprizeMimeMessage implements MimeMessagePreparator {

	private final String from;

	private final EmailItem emailItem;

	public LotusprizeMimeMessage(String from, EmailItem emailItem) {
		super();
		this.from = from;
		this.emailItem = emailItem;
	}

	@Override
	public void prepare(MimeMessage mimeMessage) throws Exception {
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
				true, ApplicationProperty.instance().encoding());
		mimeMessageHelper.setFrom(from);
		mimeMessageHelper.setTo(InternetAddress.parse(emailItem.getAddress()));
		mimeMessageHelper.setSubject(emailItem.getBody().getSubject());
		mimeMessageHelper.setText(emailItem.getBody().getText(), true);
	}

}
