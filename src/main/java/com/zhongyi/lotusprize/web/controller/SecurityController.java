package com.zhongyi.lotusprize.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class SecurityController extends BaseController{
	
	@RequestMapping(value="/account/auth")
	public void userAuthenticate(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> authInfo = Maps.newHashMap();
		boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
		authInfo.put("authenticated", authenticated);
		if(authenticated){
			authInfo.put("accountId", WebUtil.currentUserAccountId());
			authInfo.put("roleName", Roles.roleNames((Integer)WebUtil.currentSession(false).getAttribute("roleValue")));
		}
		outputJson(authInfo,request,response);
	}

}
