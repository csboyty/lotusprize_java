package com.zhongyi.lotusprize.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.DocumentException;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.artifact.Artifact;
import com.zhongyi.lotusprize.domain.artifact.ArtifactIntroduce;
import com.zhongyi.lotusprize.redis.AccountRedis;
import com.zhongyi.lotusprize.service.artifact.ArtifactPdfHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactResultHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactUserOperationHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactUserPermissionHandler;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class UserArtifactController extends BaseController {
	
	@Autowired
	private ArtifactUserOperationHandler artifactUserOperationHandler;
	
	@Autowired
	private ArtifactRetriveHandler artifactRetriveHandler;
	
	@Autowired
	private TopicRetriveHandler topicRetriveHandler;
	
	@Autowired
	private ArtifactResultHandler artifactResultHandler;
	
	@Autowired
	private ArtifactPdfHandler artifactPdfHandler;
	
	@Autowired
	private ArtifactUserPermissionHandler artifactUserPermissionHandler;
	
	@Autowired
	private AccountRedis accountRedis;
	
	
	
	
	@RequestMapping(value="/user/artifact",method=RequestMethod.GET)
	public ModelAndView showUserArtifactPage(HttpServletRequest request,HttpServletResponse response){
		String lang = WebUtil.userLang(request);
		String viewName = WebUtil.localeView(lang, "user/myWorkMgr");
		ModelAndView mav =  new ModelAndView(viewName);
		Collection<Map<String,Object>> artifacts = artifactRetriveHandler.userArtifacts(WebUtil.currentUserAccountId(), lang);
		mav.addObject("artifacts", artifacts);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="/user/artifact/create",method=RequestMethod.GET)
	public ModelAndView showCreateArtifactPage(	@RequestParam(value="topicId")Integer topicId,
			HttpServletRequest request,HttpServletResponse response){
		String lang = WebUtil.userLang(request);
		String viewName = WebUtil.localeView(lang, "user/uploadWork");
		ModelAndView mav =  new ModelAndView(viewName);
		mav.addObject("topicId", topicId);
		mav.addObject("topicName",topicRetriveHandler.getNameByTopicId(topicId, lang));
		Account account = accountRedis.accountById(WebUtil.currentUserAccountId());
		mav.addObject("organization",account.getAccountProfile().getOrganization());
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="/user/artifact/update/{artifactId}",method=RequestMethod.GET)
	public ModelAndView showUpdateArtifactPage(	@PathVariable("artifactId")Integer artifactId,
			HttpServletRequest request,HttpServletResponse response){
		String lang = WebUtil.userLang(request);
		String viewName = WebUtil.localeView(lang, "user/uploadWork");
		ModelAndView mav =  new ModelAndView(viewName);
		Map<String,Object> artifactMap = artifactRetriveHandler.artifactById(artifactId, lang);
		mav.addObject("artifact", artifactMap);
		mav.addObject("topicId", artifactMap.get("topicId"));
		mav.addObject("topicName", artifactMap.get("topicName"));
		addObjects(mav);
		return mav;
	}
	
	
	@RequestMapping(value="/user/artifact/show/{artifactId}",method=RequestMethod.GET)
	public ModelAndView showArtifactPage(@PathVariable("artifactId")Integer artifactId,
	        @RequestParam(value="clientId",required=false)String clientId,
			HttpServletRequest request,HttpServletResponse response){
		String lang = WebUtil.userLang(request);
		String viewName = WebUtil.localeView(lang, "user/workDetail");
		ModelAndView mav =  new ModelAndView(viewName);
		boolean readable = artifactUserPermissionHandler.hasReadPermision(artifactId);
		Map<String,Object> artifactMap = artifactRetriveHandler.artifactById(artifactId, lang);
		mav.addObject("artifact", artifactMap);
		addFormToken("voteToken",mav);
		if(!Strings.isNullOrEmpty(clientId)){
		    mav.addObject("voteExist", artifactResultHandler.isVoteExist(artifactId, clientId));
		}
		mav.addObject("hasDetailPermit", readable);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="/user/artifact/show/{artifactId}/introduce",method=RequestMethod.GET)
	public void artifactIntroduce(@PathVariable("artifactId")Integer artifactId,
			HttpServletRequest request,HttpServletResponse response){
		Collection<Map<String,Object>> introduces = artifactRetriveHandler.artifactIntroduceById(artifactId);
		outputJson(introduces,request,response);
		
	}
	
	@RequestMapping(value="/user/artifact/createOrUpdate",method=RequestMethod.POST)
	public void saveArtifact(@RequestParam(value="artifactId",required=false)Integer artifactId,
			@RequestParam(value="topicId")Integer topicId,
			@RequestParam(value="title")String title,
			@RequestParam(value="description")String description,
			@RequestParam(value="profile")String profile,
			@RequestParam(value="organization")String organization,
			@RequestParam(value="author")String author,
			@RequestParam(value="attachment")String attachment,
			@RequestParam(value="introduce")String introduceJson,
			HttpServletRequest request,HttpServletResponse response) throws IOException{
		Date now = new Date();
		Artifact artifact = new Artifact();
		artifact.setId(artifactId);
		artifact.setTopicId(topicId);
		artifact.setOwnAccountId(WebUtil.currentUserAccountId());
		artifact.setTitle(title);
		artifact.setDescription(description);
		artifact.setProfile(profile);
		artifact.setOrganizations(organization);
		artifact.setAuthors(author.replaceAll("；", ";"));
		artifact.setAttachment(attachment);
		Collection<ArtifactIntroduce> introduces = parseIntroduceJson(introduceJson);
		artifact.setIntroduces(introduces);
		artifact.setCreateTime(now);
		if(artifactId == null){
		    artifact.setStatus((short)1);
		}
		artifactUserOperationHandler.save(artifact);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	

	@RequestMapping(value="/user/artifact/remove",method=RequestMethod.POST)
	public void remove(@RequestParam(value="artifactId")Integer artifactId,
			HttpServletRequest request,HttpServletResponse response){
		
		artifactUserOperationHandler.remove(WebUtil.currentUserAccountId(), artifactId);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	
	
	@RequestMapping(value="/user/artifact/pdf/{artifactId}")
	public void pdfExport(@PathVariable("artifactId")Integer artifactId,
            HttpServletRequest request,HttpServletResponse response) throws IOException, DocumentException{
	    boolean readable = artifactUserPermissionHandler.hasReadPermision(artifactId);
	    if(readable){
    	    String lang = WebUtil.userLang(request);
    	    boolean showAuthorInfo = false;
    	    if(SecurityUtils.getSubject().isAuthenticated()){
    	        Integer roleValue= WebUtil.currentUserRoleValue();
    	        if(roleValue!= null && (Roles.adminRole.equals(Roles.highestRole(roleValue))))
    	            showAuthorInfo = true; 
    	    }
    	    Map<String,Object> result= artifactPdfHandler.exportAsPdf(artifactId,showAuthorInfo,lang);
    	    String filename = (String)result.get("filename");
    	    InputStream in = new FileInputStream((File)result.get("file"));
    	    outputBinary(filename,in,"application/octet-stream",request,response);
	    }else{
	        response.sendError(403);
	    }
	}
	
	
	private Collection<ArtifactIntroduce> parseIntroduceJson(String introduceJson){
		Map<Integer,Map<String,String>> introduceMap = JsonUtil.fromJson(new TypeReference<Map<Integer,Map<String,String>>>(){},introduceJson);
		Collection<ArtifactIntroduce> introduces = Lists.newArrayListWithCapacity(introduceMap.size());
		for(Map.Entry<Integer, Map<String,String>> introduceEntry:introduceMap.entrySet()){
			ArtifactIntroduce introduce = new ArtifactIntroduce();
			introduce.setPos(introduceEntry.getKey());
			introduce.setText(introduceEntry.getValue().get("text"));
			introduce.setImage(introduceEntry.getValue().get("image"));
			introduces.add(introduce);
		}
		return introduces;
	}

}
