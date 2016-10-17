package com.zhongyi.lotusprize.web.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.account.AccountOperationHandler;
import com.zhongyi.lotusprize.service.artifact.Artifact2ExpertHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactExpertOperationHandler;
import com.zhongyi.lotusprize.service.topic.StageSettingHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/expert")
public class ExpertArtifactController extends BaseController{
	
	@Autowired
	private Artifact2ExpertHandler artifact2ExpertHandler;
	
	@Autowired
	private ArtifactExpertOperationHandler artifactExpertOperationHandler;
	
	@Autowired
    private TopicRetriveHandler topicRetriveHandler;
	
	@Autowired
	private AccountOperationHandler accountOperationHandler;
	
	
	@RequestMapping(value="home",method=RequestMethod.GET)
	public ModelAndView expertHomePage(
	        HttpServletRequest request,HttpServletResponse response){
	    Integer expertId = WebUtil.currentUserAccountId();
	    Short round = StageSettingHandler.getInstance().roundByStage();
	    String lang = WebUtil.userLang(request);
		String viewName = WebUtil.localeView(lang, "judge/home");
		ModelAndView mav = new ModelAndView(viewName);
		if(round == 1)
		    mav.addObject("expertTopics", artifact2ExpertHandler.listExpertTopic(expertId,round, lang));
		else
		    mav.addObject("expertTopicsByCategory", artifact2ExpertHandler.listExpertTopicRound2ByCategory(expertId));
		mav.addObject("expertName",accountOperationHandler.accountById(expertId).getFullname());
		mav.addObject("lang", lang);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="topic/{topicId}",method=RequestMethod.GET)
	public ModelAndView showTopicArtifactPage(@PathVariable("topicId")Integer topicId,
	        @RequestParam(value="categoryStatus",required=false) String categoryStatus,
	        HttpServletRequest request,HttpServletResponse response){
	    Integer expertId = WebUtil.currentUserAccountId();
	    String lang = WebUtil.userLang(request);
	    String viewName =  WebUtil.localeView(lang, "judge/showWork");
	    ModelAndView mav = new ModelAndView(viewName);
	    Short round = StageSettingHandler.getInstance().roundByStage();
	    
	    if(round == 1){
    	    Map<String,Object> topicMap = topicRetriveHandler.getTopicAsMap(topicId, lang, false);
    	    if(topicMap == null || topicMap.isEmpty()){
    	        topicMap = Collections.emptyMap();
            }else{
                Map<String,Object> expertTopicStatus = artifact2ExpertHandler.getExpertTopicRoundStatus(expertId, topicId,round);
                topicMap.putAll(expertTopicStatus);
            }
    	    mav.addObject("topic", topicMap);
	    }else{
	        // round2 topicId 代表 category,categoryStatus 代表  作品数和已打分作品数,用逗号分隔
	        mav.addObject("category", topicId);
	        if(!Strings.isNullOrEmpty(categoryStatus)){
	            String[] categoryStatusValue = categoryStatus.split(",");
	            mav.addObject("cnt", Ints.tryParse(categoryStatusValue[0]));
	            mav.addObject("scoredCnt", Ints.tryParse(categoryStatusValue[1]));
	        }
	    }
        addObjects(mav);
        return mav;
	}
	
	
	
	
	@RequestMapping(value="topic/artifact/list",method=RequestMethod.GET)
	public void listArtifact(@RequestParam(value="searchTopic",required=false)Integer topicId,
	        @RequestParam(value="searchCategory",required=false)Short category,
	        @RequestParam(value="round")Short round,
	        @RequestParam(value="searchTitle",required=false)String artifactTitle,
	        @RequestParam(value="showOption",defaultValue="all")String showOption,
	        DataTablesParameter dtParameter, HttpServletRequest request,
			HttpServletResponse response){
	    
		Integer expertId= WebUtil.currentUserAccountId();
		int offset = dtParameter.getiDisplayStart();
		int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter	.getiDisplayLength();
		SqlQueryResult<Map<String,Object>> sqlResult;
		if(round ==1 )
		    sqlResult = artifactExpertOperationHandler.listByTopic(expertId,topicId,round,artifactTitle,showOption,offset, limit);
		else
		    sqlResult = artifactExpertOperationHandler.listByTopicByCategoryRound2(category, expertId, showOption);
		DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
				dtParameter.getsEcho(), sqlResult.totalRecords,
				sqlResult.totalRecords, sqlResult.data);
		outputJson(result, request, response);
	}
	
	@RequestMapping(value="topic/artifact/doScore",method=RequestMethod.POST)
	public void doScore(@RequestParam(value="artifactId")Integer artifactId,
	        @RequestParam(value="round")Short round,
			@RequestParam(value="score")Integer score,
			HttpServletRequest request,HttpServletResponse response){
		Integer expertId= WebUtil.currentUserAccountId();
		artifactExpertOperationHandler.doArtifactScore(expertId,artifactId,round,score);
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", true);
		outputJson(result, request, response);
	}
	

}
