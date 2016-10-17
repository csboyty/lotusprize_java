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

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.service.account.UpdateAccountHandler;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class AccountSettingController extends BaseController {

	@Autowired
	private UpdateAccountHandler accountHandler;
	
	@RequestMapping(value = "/account/info", method = RequestMethod.GET)
	public ModelAndView showInfoPage(HttpServletRequest request, HttpServletResponse response){
		String viewName = WebUtil.localeView(request, "user/changeProfile");
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("account", accountHandler.currentAccount());
		addObjects(mav);
		return mav;
	}

	@RequestMapping(value = "/account/info", method = RequestMethod.POST)
	public void changeInfo(@RequestParam(value = "fullname") String fullname,
			@RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "address") String address,
			@RequestParam(value = "gender") Byte gender,
			@RequestParam(value = "memo",required=false) String memo,
			@RequestParam(value = "organization",required=false) String organization,
			HttpServletRequest request, HttpServletResponse response) {
		accountHandler.changeInfo(fullname, mobile, address, gender,
				memo, organization);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	
	@RequestMapping(value = "/account/password", method = RequestMethod.GET)
	public ModelAndView showChangePasswordPage(HttpServletRequest request, HttpServletResponse response){
		String viewName = WebUtil.localeView(request, "user/resetPwd");
		ModelAndView mav =  new ModelAndView(viewName);
		addObjects(mav);
		return mav;
	}

	@RequestMapping(value = "/account/password", method = RequestMethod.POST)
	public void changePassword(
			@RequestParam(value = "oldPassword") String oldPassword,
			@RequestParam(value = "newPassword") String newPassword,
			HttpServletRequest request, HttpServletResponse response) {
		
		accountHandler.changePassword(oldPassword, newPassword);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}

}
