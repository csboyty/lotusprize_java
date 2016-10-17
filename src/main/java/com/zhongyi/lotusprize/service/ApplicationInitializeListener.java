package com.zhongyi.lotusprize.service;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationInitializeListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		try(InputStream in = ApplicationInitializeListener.class.getClassLoader()
				.getResourceAsStream("application.properties")){
			Properties properties = new Properties();
			properties.load(in);
			ApplicationProperty.instance().initialize(servletContext, properties);
			AppListeningExecutorService.instance().init();
		}catch(Exception ex){
			throw new RuntimeException("Application initialize error",ex);
		}
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		AppListeningExecutorService.instance().destroy();
		
	}

}
