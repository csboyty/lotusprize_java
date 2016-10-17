package com.zhongyi.lotusprize.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.auth.AccountInactiveException;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.account.UpdateAccountHandler;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class LoginController extends BaseController{
	
	@Autowired
	private UpdateAccountHandler accountHandler;
	
	@RequestMapping(value="/authenticateStatus",method=RequestMethod.GET)
	public void ajaxAuthenticateStatus(@RequestParam(value="callback") String callback,
	        HttpServletRequest request,HttpServletResponse response){
	    Map<String,Object> authInfo = Maps.newHashMap();
        boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
        authInfo.put("authenticated", authenticated);
        if(authenticated){
            authInfo.put("accountId", WebUtil.currentUserAccountId());
            authInfo.put("roleName", Roles.roleNames((Integer)WebUtil.currentSession(false).getAttribute("roleValue")));
        }
        String jsonString = JsonUtil.toJsonString(authInfo);
        outputText(callback+"("+jsonString+")",request,response);
	    
	    
	}
	

	@RequestMapping(value="/login",method=RequestMethod.GET)
	public ModelAndView loginPage(HttpServletRequest request,HttpServletResponse response) throws IOException{
	    String lang = null;
	    Session s = WebUtil.currentSession(false);
	    if(s!=null){
	        lang =(String) s.getAttribute("_lang");
	    }
	    if(Strings.isNullOrEmpty(lang))
	        lang= WebUtil.userLang(request);
	    if(SecurityUtils.getSubject().isAuthenticated()){
	        Map<String,String> queryParams = Maps.newHashMap();
            queryParams.put("_lang", lang);
	        String redirectUrl = ApplicationProperty.instance().getAsString("context.url")+"/s/"+Roles.highestRole(WebUtil.currentUserRoleValue()).name();
	        org.apache.shiro.web.util.WebUtils.issueRedirect(request, response, redirectUrl, queryParams, false);
            return null;
	    }
		String viewName = WebUtil.localeView(lang, "login");
		ModelAndView mav = new ModelAndView(viewName);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ModelAndView login(@RequestParam(value="username") String username,
			@RequestParam(value="password")String password,
			@RequestParam(value="rememberMe",defaultValue="true",required=false)Boolean rememberMe,
			@RequestParam(value="loginToken")String inputLoginToken,
			HttpServletRequest request,HttpServletResponse response) throws IOException{
		
	    
		Session session = SecurityUtils.getSubject().getSession(false);
		ErrorCode errorCode = null;
		if(session == null){
			errorCode  =ErrorCode.timeout;
		}else{
//			String userCaptcha = (String)session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
			String loginToken = (String)session.removeAttribute("loginToken");
			if(Strings.isNullOrEmpty(inputLoginToken) ||  !inputLoginToken.equalsIgnoreCase(loginToken)){
				errorCode = ErrorCode.captcha_unmatch;
			}else{
				String host = WebUtil.remoteHost(request);
				UsernamePasswordToken token = new UsernamePasswordToken(username,password,rememberMe,host);
				try{
					Subject subject = SecurityUtils.getSubject();
					subject.login(token);
				}catch(AccountInactiveException ex){
					errorCode = ErrorCode.account_inactive;
				}catch(AuthenticationException ex){
					errorCode = ErrorCode.account_authenticate;
				}
			}
			
		}
		
		String lang = WebUtil.userLang(request);
		if(errorCode != null){
			String viewName = WebUtil.localeView(lang, "login");
			ModelAndView mav = new ModelAndView(viewName);
			mav.addObject("errorCode",errorCode);
			addObjects(mav);
			return mav;
		}else{
			Account account = accountHandler.currentAccount();
			WebUtil.setUserRoleValue(account.getRoleValue());
			org.apache.shiro.web.util.SavedRequest savedRequest = org.apache.shiro.web.util.WebUtils.getAndClearSavedRequest(request);
			String redirectUrl = null;
			if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(AccessControlFilter.GET_METHOD)) {
				redirectUrl = savedRequest.getRequestUrl();
	        }
			
			if(Strings.isNullOrEmpty(redirectUrl)){
				redirectUrl = ApplicationProperty.instance().getAsString("context.url")+"/s/"+Roles.highestRole(account.getRoleValue()).name();
			}
			Map<String,String> queryParams = Maps.newHashMap();
			
			if(savedRequest!=null ){
			    if(savedRequest.getQueryString() ==null || !savedRequest.getQueryString().contains("_lang"))
			        queryParams.put("_lang", lang);
			}else{
			    queryParams.put("_lang", lang);
			}
			org.apache.shiro.web.util.WebUtils.issueRedirect(request, response, redirectUrl, queryParams, false);
			return null;
		}
	}
	
	
	
}
