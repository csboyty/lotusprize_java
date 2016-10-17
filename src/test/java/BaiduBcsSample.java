/***************************************************************************
 * 
 * Copyright (c) 2012 Baidu.com, Inc. All Rights Reserved
 * 
 **************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.auth.BCSSignCondition;
import com.baidu.inf.iis.bcs.http.HttpMethodName;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BucketSummary;
import com.baidu.inf.iis.bcs.model.Empty;
import com.baidu.inf.iis.bcs.model.ObjectListing;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.ObjectSummary;
import com.baidu.inf.iis.bcs.model.Resource;
import com.baidu.inf.iis.bcs.model.SuperfileSubObject;
import com.baidu.inf.iis.bcs.model.X_BS_ACL;
import com.baidu.inf.iis.bcs.policy.Policy;
import com.baidu.inf.iis.bcs.policy.PolicyAction;
import com.baidu.inf.iis.bcs.policy.PolicyEffect;
import com.baidu.inf.iis.bcs.policy.Statement;
import com.baidu.inf.iis.bcs.request.CreateBucketRequest;
import com.baidu.inf.iis.bcs.request.GenerateUrlRequest;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.ListBucketRequest;
import com.baidu.inf.iis.bcs.request.ListObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.request.PutSuperfileRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.baidu.inf.iis.bcs.utils.Mimetypes;
import com.google.common.io.ByteSource;
import com.zhongyi.lotusprize.service.bcs.LotusprizeBcsPolicy;
import com.zhongyi.lotusprize.service.bcs.LotusprizeBcsStatement;


public class BaiduBcsSample {
	private static final Logger log = LoggerFactory.getLogger(BaiduBcsSample.class);
	// ----------------------------------------
	static String host = "bcs.duapp.com";
	static String accessKey = "93DSyi9sH4BQjjRtOGzA6Csp";
	static String secretKey = "T7iAt3i0BN9xzGXwO8ND2T8cUViuznVD";
	static String bucket = "lotusprize";
	// ----------------------------------------
	static String object = "/Declaration.pdf";
	
	
//	object = "/9/Jellyfish(533eadc80cf295248baeee7c).jpg";
	
	static File destFile = new File("test");
	private static BaiduBCS baiduBCS;
	
	@BeforeClass
	public static void init(){
		BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
		baiduBCS = new BaiduBCS(credentials, host);
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
	}
	@Test
	public  void generateUrl() {
		GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(HttpMethodName.GET, bucket, "/1/*");
		generateUrlRequest.setBcsSignCondition(new BCSSignCondition());
//		generateUrlRequest.getBcsSignCondition().setIp("1.1.1.1");
//		generateUrlRequest.getBcsSignCondition().setTime(123455L);
//		generateUrlRequest.getBcsSignCondition().setSize(123455L);
		System.out.println(baiduBCS.generateUrl(generateUrlRequest));
	}
	
	public void copyObject(String destBucket, String destObject) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("image/jpeg");
		baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject), objectMetadata);
		baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject), null);
		baiduBCS.copyObject(new Resource(bucket, object), new Resource(destBucket, destObject));
	}

	@Test
	public void createBucket() {
		// baiduBCS.createBucket(bucket);
		baiduBCS.createBucket(new CreateBucketRequest(bucket, X_BS_ACL.PublicRead));
	}
	@Test
	public  void deleteBucket() {
		baiduBCS.deleteBucket(bucket);
	}
	@Test
	public  void deleteObject() {
		Empty result = baiduBCS.deleteObject(bucket, object).getResult();
		log.info("",result);
	}
	@Test
	public void getBucketPolicy() {
		BaiduBCSResponse<Policy> response = baiduBCS.getBucketPolicy(bucket);
		log.info("After analyze: " + response.getResult().toJson());
		log.info("Origianal str: " + response.getResult().getOriginalJsonStr());
	}
	

	@Test
	public void getObjectMetadata() {
		ObjectMetadata objectMetadata = baiduBCS.getObjectMetadata(bucket, object).getResult();
		System.out.println(objectMetadata);
	}
	@Test
	public void getObjectPolicy() {
//		String object = "/1/SizeOf_0_2_1(5317db65fc5f77f86850f18b).zip";
//		String object = "/1/12-08-2423-03-07(5317db76fc5f77f86850f18c).png";
		BaiduBCSResponse<Policy> response = baiduBCS.getObjectPolicy(bucket, object);
		log.info("After analyze: " + response.getResult().toJson());
		log.info("Origianal str: " + response.getResult().getOriginalJsonStr());
	}
	@Test
	public void getObjectWithDestFile() {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, object);
		baiduBCS.getObject(getObjectRequest, destFile);
	}

	@Test
	public void listBucket() {
		ListBucketRequest listBucketRequest = new ListBucketRequest();
		BaiduBCSResponse<List<BucketSummary>> response = baiduBCS.listBucket(listBucketRequest);
		for (BucketSummary bucket : response.getResult()) {
			log.info("",bucket);
		}
	}
	@Test
	public void listObject() {
		ListObjectRequest listObjectRequest = new ListObjectRequest(bucket);
		listObjectRequest.setStart(0);
		listObjectRequest.setLimit(20);
		// ------------------by dir
		{
			// prefix must start with '/' and end with '/'
			// listObjectRequest.setPrefix("/1/");
			// listObjectRequest.setListModel(2);
		}
		// ------------------only object
		{
			// prefix must start with '/'
			// listObjectRequest.setPrefix("/1/");
		}
		BaiduBCSResponse<ObjectListing> response = baiduBCS.listObject(listObjectRequest);
		log.info("we get [" + response.getResult().getObjectSummaries().size() + "] object record.");
		for (ObjectSummary os : response.getResult().getObjectSummaries()) {
			log.info(os.toString());
		}
	}
	
	@Test
	public void putBucketPolicyByPolicy() {
		Policy policy = new Policy();
		Statement st1 = new Statement();
		st1.addAction(PolicyAction.get_object);
		st1.addUser("zhengkan").addUser("zhangyong01");
		st1.addResource(bucket + "/111").addResource(bucket + "/111");
		st1.setEffect(PolicyEffect.allow);
		policy.addStatements(st1);
		baiduBCS.putBucketPolicy(bucket, policy);
	}

	@Test
	public void putBucketPolicyByX_BS_ACL() {
		baiduBCS.putBucketPolicy(bucket, X_BS_ACL.PublicRead);
	}
	@Test
	public void putObjectByFile() throws IOException {
		String object = "/images/sub_image.jpg";
		File file = new File("images/sub_image.jpg");
		PutObjectRequest request = new PutObjectRequest(bucket, object,file);
		ObjectMetadata metadata = new ObjectMetadata();
		String contentType = Files.probeContentType(file.toPath());
		metadata.setContentType(contentType);
		metadata.setContentLength(file.length());
//		metadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
		request.setMetadata(metadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		ObjectMetadata objectMetadata = response.getResult();
		System.out.println("x-bs-request-id: " + response.getRequestId());
		System.out.println("objectMetadata"+objectMetadata);
	}

	@Test
	public void putObjectByInputStream() throws FileNotFoundException {
		File file = createSampleFile();
		InputStream fileContent = new FileInputStream(file);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("text/html");
		objectMetadata.setContentLength(file.length());
		PutObjectRequest request = new PutObjectRequest(bucket, object, fileContent, objectMetadata);
		ObjectMetadata result = baiduBCS.putObject(request).getResult();
		log.info("",result);
	}

	@Test
	public void putObjectPolicyUseLotusprizeBcsStatement() {
		String object = "/1/SizeOf_0_2_1(5317db65fc5f77f86850f18b).zip";
		LotusprizeBcsPolicy policy = new LotusprizeBcsPolicy();
		LotusprizeBcsStatement st1 = new LotusprizeBcsStatement();
		st1.addAction(PolicyAction.get_object).addUser("*").addResource(bucket + object);
		st1.addReferer("http://www.baidu.com/*");
		st1.setEffect(PolicyEffect.allow);
		policy.addStatements(st1);
		baiduBCS.putObjectPolicy(bucket, object, policy);
	}

	public void putObjectPolicyByX_BS_ACL(X_BS_ACL acl) {
		baiduBCS.putObjectPolicy(bucket, object, acl);
	}

	@Test
	public  void putSuperfile() {
		String object = "/super-test.txt";
		List<SuperfileSubObject> subObjectList = new ArrayList<SuperfileSubObject>();
		// 0
		BaiduBCSResponse<ObjectMetadata> response1 = baiduBCS.putObject(bucket, object + "_part0", createSampleFile());
		subObjectList.add(new SuperfileSubObject(bucket, object + "_part0", response1.getResult().getETag()));
		// 1
		BaiduBCSResponse<ObjectMetadata> response2 = baiduBCS.putObject(bucket, object + "_part1", createSampleFile());
		subObjectList.add(new SuperfileSubObject(bucket, object + "_part1", response2.getResult().getETag()));
		// put superfile
		PutSuperfileRequest request = new PutSuperfileRequest(bucket, object + "_superfile", subObjectList);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putSuperfile(request);
		ObjectMetadata objectMetadata = response.getResult();
		log.info("x-bs-request-id: " + response.getRequestId());
		log.info("",objectMetadata);
	}
	
	@Test
	public void putVideoFile(){
		List<SuperfileSubObject> subObjectList = new ArrayList<SuperfileSubObject>();
		File videoFile = new File("D:/kankan/metalS0603.mp4");
		ByteSource videoSource =com.google.common.io.Files.asByteSource(videoFile);
		int sliceLength = 10 * 1024 * 1024;
		long offset = 0;
		long fileLength = videoFile.length();
		int partIndex = 0;
		Exception ex = null;
		while((offset+sliceLength) < fileLength){
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(videoFile));
			objectMetadata.setContentLength(sliceLength);
			ByteSource sliceSource = videoSource.slice(offset, sliceLength);
			String sliceName ="/"+ videoFile.getName() + "_part"+partIndex;
			try {
				BaiduBCSResponse<ObjectMetadata> sliceResponse = baiduBCS.putObject(bucket,sliceName ,sliceSource.openBufferedStream(),objectMetadata);
				subObjectList.add(new SuperfileSubObject(bucket,sliceName,sliceResponse.getResult().getETag()));
				offset += sliceLength;
				partIndex ++;
			} catch (BCSClientException | IOException e) {
				ex = e;
				break;
			}
			
		}
		if(ex == null && offset < fileLength){
			long lastLength = fileLength-offset;
			ByteSource sliceSource = videoSource.slice(offset, lastLength);
			String sliceName = "/"+videoFile.getName() + "_part"+partIndex;
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(videoFile));
			objectMetadata.setContentLength(lastLength);
			BaiduBCSResponse<ObjectMetadata> sliceResponse;
			try {
				sliceResponse = baiduBCS.putObject(bucket,sliceName ,sliceSource.openBufferedStream(),objectMetadata);
				subObjectList.add(new SuperfileSubObject(bucket,sliceName,sliceResponse.getResult().getETag()));
			} catch (BCSClientException | IOException e) {
				ex = e;
			}
			
		}
		
		if(ex == null ){
			PutSuperfileRequest request = new PutSuperfileRequest(bucket,"/"+ videoFile.getName() + "_superfile", subObjectList);
			BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putSuperfile(request);
			ObjectMetadata objectMetadata = response.getResult();
			System.out.println("x-bs-request-id: " + response.getRequestId());
			System.out.println("objece metadata:"+objectMetadata);
		}
		
				
	}
	
	@Test
	public void putAntiSteal() throws ClientProtocolException, IOException{
		String object = "/1/12-08-2423-03-07(5317db76fc5f77f86850f18c).png";
		GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(HttpMethodName.PUT, bucket,object);
		String url = baiduBCS.generateUrl(generateUrlRequest);
		url = url+"&acl=1";
		System.out.println(url);
		String response = Request.Put(url).bodyString(antiSteal(),ContentType.APPLICATION_JSON).execute().returnContent().asString();
		System.out.println(response);
		
	}

	@Test
	public  void setObjectMetadata() {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("application/pdf");
		baiduBCS.setObjectMetadata(bucket, object, objectMetadata);
	}

	private static File createSampleFile() {
		try {
			File file = File.createTempFile("java-sdk-", ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write("01234567890123456789\n");
			writer.write("01234567890123456789\n");
			writer.write("01234567890123456789\n");
			writer.write("01234567890123456789\n");
			writer.write("01234567890123456789\n");
			writer.close();

			return file;
		} catch (IOException e) {
			log.error("tmp file create failed.");
			return null;
		}
	}
	
	private String antiSteal(){
		return "{'statements':[{'action':['get_object'],'effect':'allow','resource':['lotusprize/1/12-08-2423-03-07(5317db76fc5f77f86850f18c).png'],'user':['*'],'referer':['http://www.baidu.com/*']}]}";
	}
	
	
}