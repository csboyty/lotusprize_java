package com.zhongyi.lotusprize.service.bcs;

import java.io.File;
import java.lang.reflect.Field;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Component;

import com.baidu.inf.iis.bcs.http.BCSHttpClient;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.policy.PolicyAction;
import com.baidu.inf.iis.bcs.policy.PolicyEffect;
import com.baidu.inf.iis.bcs.request.PutObjectPolicyRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.google.common.eventbus.Subscribe;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.service.SystemEventBus;

@Component
public class LotusprizeBcsExecutor {
	
	private static final MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
	

	@PostConstruct
	public void init() {
        SystemEventBus.instance().register(this);
		
	}
	
	@PreDestroy
	public void destroy() throws InterruptedException{
		try {
			Field bcsHttpClientField = LotusprizeBcsFiles.instance().baiduBCS().getClass()
					.getDeclaredField("bcsHttpClient");
			bcsHttpClientField.setAccessible(true);
			BCSHttpClient bcsHttpClient = (BCSHttpClient)bcsHttpClientField.get(LotusprizeBcsFiles.instance().baiduBCS());
			Field httpClientField = bcsHttpClient.getClass().getDeclaredField("httpClient");
			httpClientField.setAccessible(true);
			HttpClient httpClient =(HttpClient) httpClientField.get(bcsHttpClient);
			httpClient.getConnectionManager().shutdown();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	@Subscribe
	public void handleFileCreate(final SystemEventBus.BcsAddFileEvent event) {
		String objectName = LotusprizeLocalFiles.instance().userPartPath(event.localHttpPath());
		PutObjectRequest putObjectRequest = createByFilePath(objectName,event.localHttpPath());
		Exception ex = null;
		try{
			LotusprizeBcsFiles.instance().baiduBCS().putObject(putObjectRequest);
			PutObjectPolicyRequest putObjectPolicyRequest =createByObjectname(objectName);
			LotusprizeBcsFiles.instance().baiduBCS().putObjectPolicy(putObjectPolicyRequest);
		}catch(BCSClientException e){
			ex = e;
		}
		if(ex != null){
			event.setEx(ex);
		}else{
			String bcsObjectCdnPath = LotusprizeBcsFiles.instance().bcsObjectCdnPath(objectName);
			event.setResult(bcsObjectCdnPath);
		}
	}
	
	@Subscribe
	public void handleFileDelete(final SystemEventBus.BcsRemoveFileEvent event) {
		String objectName = LotusprizeBcsFiles.instance().bcsObjectName(event.fileHttpPath());
		Exception ex = null;
		try{
			LotusprizeBcsFiles.instance().baiduBCS()
				.deleteObject(LotusprizeBcsFiles.instance().bcsBucketName(), objectName).getResult();
		}catch(BCSClientException e){
			ex = e;
		}
		if(ex != null){
			event.setEx(ex);
		}else{
			event.setResult(event.fileHttpPath());
		}
		
			
		

	}
	
	private PutObjectRequest createByFilePath(String objectName,String filePath){
		File localFile = LotusprizeLocalFiles.instance().toLocalFile(filePath);
		String contentType;
		if(filePath.toLowerCase().endsWith("mp4")){
			contentType = "video/mp4";
		}else{
			contentType = mimeTypes.getContentType(localFile);
		}
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		objectMetadata.setContentLength(localFile.length());
		PutObjectRequest request = new PutObjectRequest(LotusprizeBcsFiles.instance().bcsBucketName(), objectName,localFile);
		request.setMetadata(objectMetadata);
		return request;
	}
	
	private PutObjectPolicyRequest createByObjectname(String objectName){
		LotusprizeBcsPolicy policy = new LotusprizeBcsPolicy();
		LotusprizeBcsStatement st1 = new LotusprizeBcsStatement();
		st1.addAction(PolicyAction.get_object).addUser("*").
			addResource(LotusprizeBcsFiles.instance().bcsBucketName() + objectName);
		st1.setReferer(LotusprizeBcsFiles.instance().allowDomains());
		st1.setEffect(PolicyEffect.allow);
		policy.addStatements(st1);
		return new PutObjectPolicyRequest(LotusprizeBcsFiles.instance().bcsBucketName(), objectName,policy);
	}

	
	
	
	
	
	

}
