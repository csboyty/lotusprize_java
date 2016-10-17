package com.zhongyi.lotusprize.util;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import com.google.common.base.Strings;
import com.zhongyi.lotusprize.auth.LotusprizePrincipal;
import com.zhongyi.lotusprize.auth.UsernameAndPasswordRealm;
import com.zhongyi.lotusprize.exception.TimeoutError;


public class WebUtil {
	
	public static final String DEFAULT_LANG = "zh";

    public enum WebRequestType {
        ajax, no_ajax
    }

    public static WebRequestType requestType(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        WebRequestType type = "XMLHttpRequest".equalsIgnoreCase(header) ? WebRequestType.ajax : WebRequestType.no_ajax;
        return type;
    }

    public static boolean isIE(HttpServletRequest request) {
    	String userAgent = request.getHeader("User-Agent");
    	userAgent = (userAgent == null) ? "" : userAgent;
        return userAgent.toLowerCase().indexOf("msie") > 0 ? true : false;
    }

    public static String remoteHost(HttpServletRequest request){
    	 String remoteAddr = request.getRemoteAddr();
         String x;
         if ((x = request.getHeader("X-FORWARDED-FOR")) != null) {
             remoteAddr = x;
             int idx = remoteAddr.indexOf(',');
             if (idx > -1) {
                 remoteAddr = remoteAddr.substring(0, idx);
             }
         }
         return remoteAddr;
    }
    
    
    @SuppressWarnings("unchecked")
    public static Integer currentUserAccountId() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            Collection<LotusprizePrincipal> principals = SecurityUtils.getSubject().getPrincipals().fromRealm(UsernameAndPasswordRealm.REALM_NAME);
            if (principals != null && !principals.isEmpty()) {
                return principals.iterator().next().getAccountId();
            }
        }
        throw new TimeoutError();

    }

    public static Session currentSession(boolean created){
    	Session session;
    	if(!created)
    		session = SecurityUtils.getSubject().getSession(false);
    	else
    		session = SecurityUtils.getSubject().getSession();
    	return session;
    }
    
    public static String userLang(HttpServletRequest request){
    	
    	String lang = request.getParameter("_lang");
    	if(Strings.isNullOrEmpty(lang)){
    		lang = DEFAULT_LANG;
    	}
    	String viewLang;
		if("zh".equalsIgnoreCase(lang)){
			viewLang = "zh";
		}else{
			viewLang = "en";
		}
		return viewLang;
    }
    
    

    public static String localeView(HttpServletRequest request,String defaultView){
    	String viewLang = userLang(request);
		int lastSlashPos = defaultView.lastIndexOf("/");
		if("zh".equalsIgnoreCase(viewLang)){
			return defaultView;
		}else{
			if(lastSlashPos!=-1)
				return defaultView.substring(0,lastSlashPos)+"/en"+defaultView.substring(lastSlashPos);
			else
				return "en/"+defaultView;
		}
    }
    
    
    public static String localeView(String viewLang,String defaultView){
		int lastSlashPos = defaultView.lastIndexOf("/");
		if("zh".equalsIgnoreCase(viewLang)){
			return defaultView;
		}else{
			if(lastSlashPos!=-1)
				return defaultView.substring(0,lastSlashPos)+"/en"+defaultView.substring(lastSlashPos);
			else
				return "en/"+defaultView;
		}
    }
    
    
    public static Integer currentUserRoleValue(){
    	Session s = currentSession(false);
    	if(s != null){
    		return (Integer)s.getAttribute("roleValue");
    	}
    	return null;
    }
  
    public static void setUserRoleValue(Integer roleValue){
    	Session s = currentSession(false);
    	if(s != null){
    		s.setAttribute("roleValue", roleValue);
    	}
    }
    
    
}

