package com.zhongyi.lotusprize.web.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.LotusprizeLocalFiles;
import com.zhongyi.lotusprize.util.ObjectId;
import com.zhongyi.lotusprize.util.SimpleImageInfo;

@Controller
public class UploadController extends BaseController{
	
	private static final int[] illegalChars = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,32, 34, 42, 47, 58, 60, 62, 63, 92, 124};
	

	@RequestMapping(value="/upload",method=RequestMethod.POST)
	public void upload(@RequestParam("file") MultipartFile userUploadFile,
			@RequestParam(value="isThumb",defaultValue="false",required=false)Boolean isThumb,
			@RequestParam(value="isTopicProfile",defaultValue="false",required=false)Boolean isTopicProfile,
			@RequestParam(value="isWorkImg",defaultValue="false",required=false)Boolean isArtifactImage,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		
		String originalFilename = userUploadFile.getOriginalFilename();
		String uniqueFilename = toUnique(originalFilename);
		File tempFile = LotusprizeLocalFiles.instance().tempFile(uniqueFilename);
		userUploadFile.transferTo(tempFile);
		if(isThumb){
			SimpleImageInfo imageInfo = new SimpleImageInfo(tempFile);
			if(imageInfo.getHeight()!=imageInfo.getWidth()){
				throw new LotusprizeError(ErrorCode.thumb_height_not_equals_width,originalFilename);
			}
		}
		if(isTopicProfile){
			SimpleImageInfo imageInfo = new SimpleImageInfo(tempFile);
			if(imageInfo.getWidth() != 300 &&imageInfo.getHeight() !=200){
				throw new LotusprizeError(ErrorCode.topic_profile_size_illegal,originalFilename);
			}
		}
		if(isArtifactImage){
		    SimpleImageInfo imageInfo = new SimpleImageInfo(tempFile);
		    if(imageInfo.getWidth() != 1191  && imageInfo.getHeight() !=842){
                throw new LotusprizeError(ErrorCode.work_image_size_illegal,originalFilename);
            }
		}
		
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		results.put("uri", LotusprizeLocalFiles.instance().tempHttpPath(tempFile));
		outputJson(results,request,response);
	}
	
	
	static String toUnique(String originalFilename){
		String nameWithoutExtension = Files.getNameWithoutExtension(originalFilename);
		StringBuilder cleanName = new StringBuilder();
		for (int i = 0; i < nameWithoutExtension.length(); i++) {
			int c = (int)nameWithoutExtension.charAt(i);
			if (Arrays.binarySearch(illegalChars, c) < 0) {
				cleanName.append((char)c);
			}
		}
		return cleanName.toString()+"("+ObjectId.get().toString()+")"+"."+Files.getFileExtension(originalFilename);
	} 

}
