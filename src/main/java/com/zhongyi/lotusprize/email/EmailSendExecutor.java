package com.zhongyi.lotusprize.email;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhongyi.lotusprize.domain.email.EmailItem;
import com.zhongyi.lotusprize.mapper.email.EmailMapper;
import com.zhongyi.lotusprize.service.ApplicationProperty;


public class EmailSendExecutor {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendExecutor.class);

    private final String defaultSender;

    private final JavaMailSender mailSender;

    private ExecutorService _mailThreadPoolExecutor;
    
    @Autowired
    private EmailMapper emailMapper;
    
    private FindWaitingEmailItemThread findWaitingEmailItemThread;

    public EmailSendExecutor(String defaultSender,JavaMailSender mailSender) {
        this.defaultSender = defaultSender;
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() {
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNameFormat("mail-pool-%d").setDaemon(true).setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("thread name:" + t.getName(), e);
            }
        });
        
        _mailThreadPoolExecutor = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS,
        		new SynchronousQueue<Runnable>(), threadFactoryBuilder.build(),new ThreadPoolExecutor.CallerRunsPolicy());
        findWaitingEmailItemThread = new FindWaitingEmailItemThread();
        findWaitingEmailItemThread.start();
    }

    @PreDestroy
    public void destory() throws InterruptedException {
    	findWaitingEmailItemThread.running = false;
    	findWaitingEmailItemThread.interrupt();
        _mailThreadPoolExecutor.shutdown();
        while (!_mailThreadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            logger.info("Awaiting completion of threads.");
        }
    }


    private void submit(final EmailItem emailItem) {
        _mailThreadPoolExecutor.submit(new SendEmailTask(emailItem));
    }
    
    private class SendEmailTask implements Runnable{
        private int reTry = 0;
        private final EmailItem emailItem;
        
        private SendEmailTask(EmailItem emailItem){
            this.emailItem = emailItem;
        }
        
        public void run() {
            Date now = new Date();
            EmailItem.Status status;
            try{
                mailSender.send(new LotusprizeMimeMessage(defaultSender,emailItem));
                status = EmailItem.Status.sent;
            }catch(Exception ex){
                logger.error("发送邮件失败,email:"+ emailItem.getAddress(),ex);
                status = EmailItem.Status.fail;
                reTry ++;
            }
            emailMapper.updateEmailItemOnSent(emailItem.getId(), now, status);
            if(status == EmailItem.Status.fail && reTry < 3){
                _mailThreadPoolExecutor.submit(this);
            }
        }
    }
    
    
    
    private class FindWaitingEmailItemThread extends Thread{
    	
    	private Integer emailItemId = 0;
    	private volatile boolean running = true;
    	
    	public FindWaitingEmailItemThread(){
    		super("find-waiting-emailitem-thread");
    		this.setDaemon(true);
    	}

		@Override
		public void run() {
			String serverId = ApplicationProperty.instance().getAsString("server.id");
			while(running){
					try {
						List<EmailItem> emailItems = emailMapper.findFirst20EmailItemOnWaiting(emailItemId, serverId);
						if(emailItems.isEmpty()){
							Thread.sleep(30000L);
						}else{
							Iterator<EmailItem> it = emailItems.iterator();
							while(it.hasNext()){
								EmailItem emailItem = it.next();
								emailItemId = emailItem.getId();
								submit(emailItem);
							}
						}
					}catch(DataAccessException ex){
						try {
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}catch (Exception ex){
					    logger.error("",ex);
					}
			}
		}
		
    	
    }
    
    
    @Subscribe
    public void onEmailEvent(EmailItem emailItem){
    	 submit(emailItem);
    }

    
    


}

