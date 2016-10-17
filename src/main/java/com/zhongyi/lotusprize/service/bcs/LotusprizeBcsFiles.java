package com.zhongyi.lotusprize.service.bcs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.google.common.base.Strings;
import com.zhongyi.lotusprize.service.ApplicationProperty;

public class LotusprizeBcsFiles {
	
	private final static LotusprizeBcsFiles _instance = new LotusprizeBcsFiles();
	
	private final String bcsBucketName;
	
	private final String bcsBucketPath;
	
	private final BaiduBCS baiduBCS;
	
	private final String bcsCdnPath;
	
	private final List<String> allowDomains;
	
	private LotusprizeBcsFiles(){
		String host =  ApplicationProperty.instance().getAsString("storage.bcs.host");
		String accessKey =  ApplicationProperty.instance().getAsString("storage.bcs.access_key");
		String secretKey = ApplicationProperty.instance().getAsString("storage.bcs.secret_key");
		bcsBucketName = ApplicationProperty.instance().getAsString("storage.bcs.bucket");
		bcsCdnPath = ApplicationProperty.instance().getAsString("storage.bcs.cdn");
		allowDomains = Collections.unmodifiableList(Arrays.asList(
				ApplicationProperty.instance().getAsString("storage.bcs.allow_domain").split(",")));
		BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
		baiduBCS = new BaiduBCS(credentials, host);
		baiduBCS.setDefaultEncoding("UTF-8");
		bcsBucketPath = "http://" + host + "/" + bcsBucketName;
		
	}
	
	public boolean isBcsFile(String uri){
		if(Strings.isNullOrEmpty(uri))return false;
		if(uri.startsWith(bcsCdnPath))return true;
		return false;
	}
	
	public String bcsBucketName(){
		return bcsBucketName;
	}
	
	public String bcsObjectName(String bcsObjectPath){
		return bcsObjectPath.substring(bcsCdnPath.length());
	}
	

	
	public BaiduBCS baiduBCS(){
		return baiduBCS;
	}
	
	public String bcsObjectCdnPath(String objectName){
		return bcsCdnPath+objectName;
	}
	
	public List<String> allowDomains(){
		return allowDomains;
	}
	
	public static LotusprizeBcsFiles instance(){
		return _instance;
	}
	
	

}
