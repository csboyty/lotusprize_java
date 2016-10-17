package com.zhongyi.lotusprize.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.ApplicationProperty;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.service.topic.StageSettingHandler;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;

public abstract class BaseController {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected void outputJson(Object object,HttpServletRequest request,HttpServletResponse response) {
        String contentType = "application/json;charset=UTF-8";
        if (WebUtil.isIE(request)) {
            contentType = "text/plain;charset=UTF-8";
        }
        try(OutputStream out=response.getOutputStream()) {
        	response.setContentType(contentType);
            response.setHeader("Cache-Control", "no-store, no-cache");
            response.setHeader("Pragma", "no-cache");
            JsonUtil.toJson(object, out);
            out.flush();
        }catch (Exception ex){
            logger.error("输出Json 结果出错",ex);
        }
    }
	
	protected void outputText(String text,HttpServletRequest request,HttpServletResponse response) {
        String contentType = "text/plain;charset=UTF-8";
        try(OutputStream out=response.getOutputStream()) {
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "no-store, no-cache");
            response.setHeader("Pragma", "no-cache");
            out.write(text.getBytes(ApplicationProperty.instance().charset()));
            out.flush();
        }catch (Exception ex){
            logger.error("输出Json 结果出错",ex);
        }
    }
	
	 protected void outputBinary(String filename, InputStream in, String contentType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!Strings.isNullOrEmpty(filename)) {
            String attchmentName = URLEncoder.encode(filename, "UTF8");
            response.setHeader("Content-Disposition", "attachment;filename="+ attchmentName);
        } else {
            response.setHeader("Content-Disposition", "inline");
        }
        if(!Strings.isNullOrEmpty(contentType)){
            response.setContentType(contentType);
        }else{
            response.setContentType("application/octet-stream");
        }
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            IOUtils.copy(in, out);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
	
	
	protected void addFormToken(String tokenName,ModelAndView mav){
		String token = UUID.randomUUID().toString();
    	Session session = WebUtil.currentSession(true);
    	session.setAttribute(tokenName, token);
    	mav.addObject(tokenName, token);
	}
	
	protected void validateFormToken(String tokenName,HttpServletRequest request){
		Session session = WebUtil.currentSession(false);
		ErrorCode errorCode = null;
	    if(session == null){
	       	errorCode = ErrorCode.timeout;
	    }else{
	    	String clientToken = request.getParameter(tokenName) == null ? "":request.getParameter(tokenName);
	    	String serverToken = (String)session.getAttribute(tokenName);
	    	if(!clientToken.equals(serverToken)){
	    		errorCode = ErrorCode.form_token_not_match;
	    	}else{
	    		session.removeAttribute(tokenName);
	    	}
	    }
	    if(errorCode != null){
	    	throw new LotusprizeError(errorCode);
	    }
		
	}
	
	protected void addObjects(ModelAndView mav){
		
		Map<String,Object> authInfo = Maps.newHashMap();
		boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
		authInfo.put("authenticated", authenticated);
		if(authenticated){
			authInfo.put("accountId", WebUtil.currentUserAccountId());
			authInfo.put("roleName", Roles.roleNames((Integer)WebUtil.currentSession(false).getAttribute("roleValue")));
		}
		mav.addObject("authInfo", authInfo);
		Integer stageValue = StageSettingHandler.getInstance().current();
		int stage = -1;
		if(stageValue != null){
		    stage = stageValue.intValue();
		}
		mav.addObject("stage",stage);
		
	}
	

}
