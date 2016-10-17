package com.zhongyi.lotusprize.web.controller;

import java.util.Collection;
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

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.artifact.ArtifactExpertOperationHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactStatusHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.Transforms;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/topicManager")
public class TopicManagerController extends BaseController{

    @Autowired
    private TopicRetriveHandler topicRetriveHandler;
    
	@Autowired
    private ArtifactExpertOperationHandler artifactExpertOperationHandler;
	
	@Autowired
    private ArtifactStatusHandler artifactStatusHandler;
	
	@Autowired
    private ArtifactRetriveHandler artifactRetriveHandler;
    
	
	@RequestMapping(value="home",method=RequestMethod.GET)
	public ModelAndView showTopicManagerHomePage(HttpServletRequest request,HttpServletResponse response){
		String lang = WebUtil.userLang(request);
		Integer accountId = WebUtil.currentUserAccountId();
		Collection<Map<String,Object>> topics = topicRetriveHandler.listByAccount(accountId, lang);
		ModelAndView mav = new ModelAndView("corpUser/home");
		mav.addObject("topics", topics);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="topic/{topicId}",method=RequestMethod.GET)
	public ModelAndView showTopicArtifactPage(@PathVariable("topicId") Integer topicId,
			HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView mav = new ModelAndView("corpUser/showWork");
		Map<String,Object> topicMap = topicRetriveHandler.getTopicAsMap(topicId, "zh", false);
		mav.addObject("topic", topicMap);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="topic/artifact/list")
    public void searchArtifact(@RequestParam(value = "searchTopic") Integer topicId,
            @RequestParam(value = "searchTitle", required = false) String title,
            @RequestParam(value = "status",required = false) String status,
            @RequestParam("orderType") String orderType,
            @RequestParam("order") String ordering,
            DataTablesParameter dtParameter, HttpServletRequest request,
            HttpServletResponse response){
        
        Integer offset = dtParameter.getiDisplayStart();
        Integer limit = dtParameter.getiDisplayLength();
        String sqlOrderby = orderbyColumn(orderType);
        String sqlOrdering = "asc".equalsIgnoreCase(ordering) ? "ASC" : "DESC";
        Iterable<Integer> statusIt = Transforms.toIntegerIter(Transforms.spliteByComma(status));
        SqlQueryResult<Map<String,Object>> sqlResult = artifactRetriveHandler.listByAdminView(topicId, title,
                statusIt,sqlOrderby,sqlOrdering, offset, limit);
        DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                dtParameter.getsEcho(), sqlResult.totalRecords,
                sqlResult.totalRecords, sqlResult.data);
        outputJson(result, request, response);
	}
	
	
    @RequestMapping(value="topic/artifact/setStatus",method=RequestMethod.POST)
    public void setArtifactStatus(@RequestParam(value="artifactId")String artifactId,
	            @RequestParam(value="status")Short status,
	            HttpServletRequest request, HttpServletResponse response){
        Iterable<Integer> artifactIdIt = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
        artifactStatusHandler.updateArtifactStatus(artifactIdIt,status);
        Map<String,Object> result = Maps.newHashMap();
        result.put("success", true);
        outputJson(result,request,response);
    }

	
    private String orderbyColumn(String orderType){
        switch(orderType){
            case "totalScore1":
                return "total_score_1";
            case "totalScore2":
                return "total_score_2";
            case "praise":
                return "total_praise";
            case "time":
            default:
                return null;
        }
    }
	
	
	
	
	
	
}
