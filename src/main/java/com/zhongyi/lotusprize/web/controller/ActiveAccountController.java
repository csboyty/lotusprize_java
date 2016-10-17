package com.zhongyi.lotusprize.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.Ciphers;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.account.ActiveAccountHandler;
import com.zhongyi.lotusprize.util.DateTimeUtil;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class ActiveAccountController extends BaseController{

	@Autowired
	private ActiveAccountHandler activeAccounthandler;

	@RequestMapping(value = "/account/active", method = RequestMethod.GET)
	public String active(
			@RequestParam(value = "inactiveAccountId") String inactiveAccountIdParam,
			@RequestParam(value = "email") String emailParam,
			@RequestParam(value = "token") String tokenParam,
			@RequestParam(value = "time") String timeParam,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) {
		Integer inactiveAccountId = Integer.parseInt(Ciphers.confirmEmailCipher
				.doDecryptUri(inactiveAccountIdParam));
		String email = Ciphers.confirmEmailCipher.doDecryptUri(emailParam);
		String token = tokenParam;
		long time = Long.parseLong(Ciphers.confirmEmailCipher.doDecryptUri(timeParam));
		
		boolean success = true;
		ErrorCode errorCode = null;
		if((System.currentTimeMillis() -time) > DateTimeUtil.one_day_mills){
			errorCode = ErrorCode.account_active_email_expired;
		}else{
			try {
				activeAccounthandler.activeUser(inactiveAccountId, email, token);
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
		return "redirect:/s/account/activeResult";
	}

	@RequestMapping(value = "/account/activeResult", method = RequestMethod.GET)
	public ModelAndView showActiveResult(
			@RequestParam(value = "success") Boolean success,
			@RequestParam(value = "errorCode", required = false) String errorCode,
			HttpServletRequest request) {
		String view = WebUtil.localeView(request, "activeResult");
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("success", success);
		mav.addObject("errorCode", errorCode);
		addObjects(mav);
		return mav;
	}

}
