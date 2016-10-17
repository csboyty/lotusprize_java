package com.zhongyi.lotusprize.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.util.WebUtil.WebRequestType;


public class CompatibleAjaxPassThruAuthenticationFilter extends AuthenticationFilter {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

    private final String resultJson = "{\"success\":false,\"errorCode\":\"timeout\"}";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        if (isLoginRequest(request, response)) {
            return true;
        } else {
            HttpServletRequest req = (HttpServletRequest)request;
            if (WebUtil.requestType(req) == WebRequestType.no_ajax){
            	if(req.getRequestURI().indexOf("/s/upload")!=-1){
            		response.setContentType("application/json");
            		response.getWriter().write(resultJson);
            		log.error("上传文件超时,remote host:"+WebUtil.remoteHost(req)+",request session id:"+(req).getRequestedSessionId());
            	}else{
            	    Session s = WebUtil.currentSession(true);
            	    s.setAttribute("_lang", WebUtil.userLang(req));
            		saveRequestAndRedirectToLogin(request, response);
            	}
            } else {
            	response.setContentType("application/json");
                response.getWriter().write(resultJson);
            }
            return false;
        }
    }

}

