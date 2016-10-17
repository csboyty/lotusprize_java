package com.zhongyi.lotusprize.service;

public enum ErrorCode {
	
	generic_error,
	
	thumb_height_not_equals_width, //图片高宽不相等
	
	topic_profile_size_illegal,//选题封面不合规格
	
	work_image_size_illegal,//作品图片不合规格,
	
	json_marshall, //对象转换为Json异常

	json_unmarshall, //json转换为对象异常
	
	timeout, //session 失效
	
	captcha_unmatch, //验证码不匹配
	
	form_token_not_match,//注册token不匹配
	
	account_inactive, //账户未激活
	
	account_authenticate, //账户验证未通过
	
	account_active_email_not_exist,//该账号不存在激活邮件或已激活
	
	account_active_email_token_not_match,//该激活邮件token不匹配
	
	account_active_email_email_duplicate,//该激活邮件email已激活
	
	account_active_email_expired,//该激活邮件已过期
	
	account_email_not_exist,//邮箱不存在
	
	account_password_not_match,//密码不匹配
	
	account_reset_password_email_expired,//该激活邮件已过期
	
	account_reset_password_email_not_exist,//该账号不存在激活邮件或已激活
	
	account_reset_password_email_token_not_match,//该激活邮件token不匹配
	
	db_conflict, //违反数据库约束
	
	op_forbidden // 禁止上传,修改,删除作品
}
