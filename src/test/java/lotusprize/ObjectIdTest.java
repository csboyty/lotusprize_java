package lotusprize;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.internet.MimeUtility;

import org.apache.shiro.codec.Base64;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.auth.PasswordSaltGenerator;
import com.zhongyi.lotusprize.util.JsonUtil;

import eu.medsea.mimeutil.MimeUtil2;

public class ObjectIdTest {

	public static void main(String[] args) throws IOException {
		
//		System.out.println(ObjectId.get().toString().length());
		
	    String s1= "agc\\f";
	    System.out.println(s1.replaceAll("\\\\", "/"));
	    System.out.println(UUID.randomUUID().toString().length());
	    System.out.println(Locale.ENGLISH.getLanguage());
	    
	    
	    Date expireDate =new LocalDate(new Date().getTime()).plusDays(3).toDate();
	    System.out.println(expireDate);
	    System.out.println(new Date());
	    
	    
//	    String json = "[{"pos":0,"text":"dessert,"good"","image":"http://s40yes.bdcdn.duapp.com/8/Desert(533d1282747f44c72db49283).jpg"},{"pos":1,"text":"很好,"verycd"","image":"http://s40yes.bdcdn.duapp.com/8/Jellyfish(533d1333747f44c72db49288).jpg"},{"pos":2,"text":"灯塔","image":"http://s40yes.bdcdn.duapp.com/8/Lighthouse(533d134b747f44c72db49289).jpg"}]";
	    
//	    String json = "{'text':'dessert,\"good\"'}";
//	    System.out.println(JsonUtil.fromJson(json).get("text"));
	    
	    Map<String,Object> map = Maps.newHashMap();
	    map.put("text", "dessert,\"good\"");
	    String json =  JsonUtil.toJsonString(map);
	    System.out.println(json);
	    System.out.println(JsonUtil.fromJson(json));
	    
	    System.out.println(URLEncoder.encode("好 ab ", "utf-8"));
	    
	    String salt = "1b06b568987012a6b436a8f0238c7098";
	    String newHashedPassword = PasswordSaltGenerator.instance().generate(salt).passwordHashed("UjDXCorY3B");
	    System.out.println(newHashedPassword);
	    
	    
	}

}
