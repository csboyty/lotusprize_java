package com.zhongyi.lotusprize.web.misc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.session.Session;
import org.apache.velocity.tools.generic.FormatConfig;

import com.google.common.base.Strings;
import com.google.common.html.HtmlEscapers;
import com.google.common.xml.XmlEscapers;
import com.zhongyi.lotusprize.util.DateTimeUtil;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;

public class LotusprizeVelocityTool extends FormatConfig{
	
	private final static Pattern lotusprizeFilenamePattern = Pattern.compile("\\([a-z0-9]{24}\\)");
	
	public String getFilename(String httpFileUri){
		String filename;
		if(Strings.isNullOrEmpty(httpFileUri))
			filename= "";
		else
			filename=FilenameUtils.getName(httpFileUri);
		
		Matcher matcher = lotusprizeFilenamePattern.matcher(filename);
		if(matcher.find()){
			filename = matcher.replaceAll("");
		}
		return filename;
	}
	
	
	public String json(Object o){
		String s=  JsonUtil.toJsonString(o);
		return s;
	}
	
	public String htmlLine(String s){
		if(s!=null && s.length()>0){
			StringBuilder buffer = new StringBuilder(s.length());
			for(char c:s.toCharArray()){
				if(c == 10){
					buffer.append("<br>");
				}else if(c == 32){
					buffer.append("&nbsp;");
				}else{
					buffer.append(c);
				}
			}
			return buffer.toString();
		}
		return s;
	}
	
	public String dateToYmd(Date date){
        return DateTimeUtil.formatAsYYYYMMdd(date);
	}

	public String escapeHtml(String s){
	    return HtmlEscapers.htmlEscaper().escape(s);
	}
	
	public String escapeXml(String s){
        return XmlEscapers.xmlContentEscaper().escape(s);
    }
	
	
	public String escapeUrlPath(String s){
	    int lastSlashPos = s.lastIndexOf("/")+1;
	    try {
            String url = s.substring(0,lastSlashPos)+URLEncoder.encode(s.substring(lastSlashPos), "utf-8");
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
           return null;
        }
	}
	
	public String loginToken(){
	    Session session= WebUtil.currentSession(true);
	    String token = RandomStringUtils.randomAlphanumeric(8);
	    session.setAttribute("loginToken", token);
	    return token;
	}
}
