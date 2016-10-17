package com.zhongyi.lotusprize.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhongyi.lotusprize.util.WebUtil;

public class CompatibleAjaxLogoutFilter extends LogoutFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String resultJson = "{\"success\":true}";

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        WebUtil.WebRequestType requestType = WebUtil.requestType((HttpServletRequest)request);

        try {
            subject.logout();
        } catch (SessionException ise) {
            logger.error("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }
        if(requestType == WebUtil.WebRequestType.no_ajax){
            String redirectUrl = getRedirectUrl(request, response, subject);
            issueRedirect(request, response, redirectUrl);
        }else{
            response.getWriter().write(resultJson);
        }
        return false;
    }



}
