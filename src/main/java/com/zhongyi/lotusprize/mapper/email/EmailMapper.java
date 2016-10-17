package com.zhongyi.lotusprize.mapper.email;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.email.AccountActiveEmail;
import com.zhongyi.lotusprize.domain.email.AccountResetPasswordEmail;
import com.zhongyi.lotusprize.domain.email.EmailBody;
import com.zhongyi.lotusprize.domain.email.EmailItem;

public interface EmailMapper {

	int insertEmailItem(EmailItem emailItem);

	int insertEmailBody(@Param("emailItemId") Integer emailItemId,
			@Param("emailBody") EmailBody emailBody);

	int insertAccountActiveEmail(AccountActiveEmail accountConfirmEmail);

	int insertOrUpdateAccountResetPasswordEmail(AccountResetPasswordEmail accountResetPasswordEmail);

	int deleteAccountActiveEmailByInactiveAccounId(Integer inactiveAccountId);

	int deleteAccountResetPasswordEmailByAccountIdAndToken(@Param("accountId")Integer accountId,@Param("token")String token);

	AccountActiveEmail accountActiveEmailByInactiveAccountId(
			Integer inactiveAccountId);
	
	AccountResetPasswordEmail accountResetPasswordEmailByAccountId(Integer accountId);

	List<EmailItem> findFirst20EmailItemOnWaiting(
			@Param("offsetEmailItemId") Integer offsetEmailItemId,
			@Param("serverId") String serverId);

	int updateEmailItemOnSent(@Param("emailItemId") Integer emailItemId,
			@Param("sendTime") Date sendTime,
			@Param("status") EmailItem.Status status);

}
