package com.zhongyi.lotusprize.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.Ciphers;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.account.ResetPasswordHandler;
import com.zhongyi.lotusprize.util.DateTimeUtil;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class ForgetPasswordController extends BaseController {

	@Autowired
	private ResetPasswordHandler resetPasswordHandler;
	

	@RequestMapping(value = "/account/forgetPassword", method = RequestMethod.GET)
	public ModelAndView showForgetPasswordPage(HttpServletRequest request,
			HttpServletResponse response) {
		String view = WebUtil.localeView(request, "forgetPwd");
		ModelAndView mav = new ModelAndView(view);
		mav.setViewName(view);
		addFormToken("forgetPasswordToken", mav);
		addObjects(mav);
		return mav;

	}

	@RequestMapping(value = "/account/forgetPassword", method = RequestMethod.POST)
	public void submitAccountEmail(@RequestParam("email") String email,
			HttpServletRequest request, HttpServletResponse response) {
		
		String lang = WebUtil.userLang(request);
		validateFormToken("forgetPasswordToken", request);
		ErrorCode errorCode = null;
		if (resetPasswordHandler.emailExist(email)) {
			resetPasswordHandler.sendResetPasswordEmail(email,lang);
		} else {
			errorCode = ErrorCode.account_email_not_exist;
		}
		Map<String, Object> results = Maps.newHashMap();
		if (errorCode == null)
			results.put("success", true);
		else{
			results.put("success", false);
			results.put("errorCode", errorCode);
		}
		outputJson(results,request,response);
	}
	
	
	@RequestMapping(value = "/account/resetPassword", method = RequestMethod.GET)
	public String active(
			@RequestParam(value = "accountId") String accountIdParam,
			@RequestParam(value = "token") String tokenParam,
			@RequestParam(value = "time") String timeParam,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) {
		Integer accountId = Integer.parseInt(Ciphers.resetPasswordCipher.doDecryptUri(accountIdParam));
		String token = tokenParam;
		long time = Long.parseLong(Ciphers.resetPasswordCipher.doDecryptUri(timeParam));
		
		boolean success = true;
		ErrorCode errorCode = null;
		if((System.currentTimeMillis() -time) > DateTimeUtil.one_day_mills){
			errorCode = ErrorCode.account_reset_password_email_expired;
		}else{
			try {
				resetPasswordHandler.resetPassword(accountId,token);
			} catch (LotusprizeError ex) {
				success = false;
				errorCode = ex.errorCode();
			} catch (Exception ex) {
				success = false;
				errorCode = ErrorCode.generic_error;
			}
		}
		redirectAttrs.addAttribute("success", success);
		redirectAttrs.addAttribute("errorCode", errorCode);
		redirectAttrs.addAttribute("_lang",WebUtil.userLang(request));
		return "redirect:/s/account/resetPasswordResult";
	}

	@RequestMapping(value = "/account/resetPasswordResult", method = RequestMethod.GET)
	public ModelAndView showResetPasswordResult(
			@RequestParam(value = "success") Boolean success,
			@RequestParam(value = "errorCode", required = false) String errorCode,
			HttpServletRequest request) {
		String view = WebUtil.localeView(request, "forgetPwdResult");
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("success", success);
		mav.addObject("errorCode", errorCode);
		addObjects(mav);
		return mav;
	}

}
