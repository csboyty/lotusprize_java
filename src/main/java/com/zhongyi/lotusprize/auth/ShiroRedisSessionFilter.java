package com.zhongyi.lotusprize.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.auth.cache.ShiroRedisSessionCache;


public class ShiroRedisSessionFilter implements Filter{
	
	@Autowired
	private ShiroRedisSessionCache cache;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("filter.....");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		try{
			chain.doFilter(request, response);
		}finally{
			cache.updateSession();
		}
		
	}

	@Override
	public void destroy() {
		
	}

}

