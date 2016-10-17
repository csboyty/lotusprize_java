package com.zhongyi.lotusprize.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class AppListeningExecutorService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final static AppListeningExecutorService _instance = new AppListeningExecutorService();
	
	private ListeningExecutorService _executor;
	
	private AppListeningExecutorService(){
		
	}
	
	void init(){
		ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
		threadFactoryBuilder.setNameFormat("lotusprize-pool-%d").setDaemon(true).setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("thread name:" + t.getName(), e);
            }
        });
        ThreadPoolExecutor threadPoolExecutor =new ThreadPoolExecutor(0, 100, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactoryBuilder.build(),new ThreadPoolExecutor.CallerRunsPolicy());
        _executor = MoreExecutors.listeningDecorator(threadPoolExecutor);
	}
	
	
	void destroy(){
		_executor.shutdown();
		try {
			while(!_executor.awaitTermination(5, TimeUnit.SECONDS)){
				logger.error("等待关闭系统线程池,继续等待5秒");
			}
		} catch (InterruptedException e) {
			logger.error("关闭系统线程池出错", e);
		}
	}
	
	public ListeningExecutorService executor(){
		return _executor;
	}
	
	
	public static AppListeningExecutorService instance(){
		return _instance;
	}
	

}
