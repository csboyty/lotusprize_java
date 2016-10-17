package com.zhongyi.lotusprize.service;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public final class ApplicationProperty {
	
	private static final ApplicationProperty _instance = new ApplicationProperty();
	
	private ApplicationProperty(){
		
	}
	
	private  final String utf8_encoding = "utf-8";
	
	private  final Charset utf8_charset = Charset.forName(utf8_encoding);
	
	private ServletContext servletContext;
	
	private Map<String,String> properties;
	
	void initialize(ServletContext servletContext,Properties properties){
		this.servletContext = servletContext;
		Map<String,String> _map = Maps.newHashMap();
		for(Map.Entry<Object, Object> entry:properties.entrySet()){
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			_map.put(key, value);
		}
		
		String sysVariable = System.getProperty("server.id");
		if(!Strings.isNullOrEmpty(sysVariable)){
		    _map.put("server.id", sysVariable);
		}
		this.properties = Collections.unmodifiableMap(_map);
	}
	
	public String encoding(){
		return utf8_encoding;
	}
	
	public Charset charset(){
		return utf8_charset;
	}
	
	public ServletContext servletContext(){
		return this.servletContext;
	}
	
	public String getAsString(String key){
		return properties.get(key);
	}
	
	public Integer getAsInt(String key){
		return Ints.tryParse(properties.get(key));
	}
	
	public Long getAsLong(String key){
		return Longs.tryParse(properties.get(key));
	}
	
	
	public Float getAsFloat(String key){
	    return Floats.tryParse(properties.get(key));
	}
	
	public static ApplicationProperty instance(){
		return _instance;
	}
	
	

}
