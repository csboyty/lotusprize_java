package com.zhongyi.lotusprize.web.controller;

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
import com.zhongyi.lotusprize.service.artifact.ArtifactResultHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactStatusHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.Transforms;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;


@Controller
public class AdminArtifactController extends BaseController {
	
	@Autowired
	private ArtifactRetriveHandler artifactRetriveHandler;
	
	@Autowired
	private TopicRetriveHandler topicRetriveHandler;
	
	@Autowired
	private ArtifactStatusHandler artifactStatusHandler;
	
	@Autowired
	private ArtifactResultHandler artifactResultHandler;
	
	
	
	@RequestMapping(value="/admin/topic/{topicId}",method=RequestMethod.GET)
	public ModelAndView showAdminArtifactPage(@PathVariable("topicId") Integer topicId,
	        @RequestParam(value="topicName")String topicName,
	        HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("admin/workMgr");
		Map<String, Object> topicInfo = Maps.newHashMap();
		topicInfo.put("id", topicId);
		topicInfo.put("name", topicName);
		mav.addObject("topic", topicInfo);
		addObjects(mav);
		return mav;
	}
	
	
	@RequestMapping(value="/admin/artifact/list")
	public void searchArtifact(@RequestParam(value = "searchTopic",required = false) Integer topicId,
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
	
	@RequestMapping(value="/admin/artifact/setStatus",method=RequestMethod.POST)
	public void setArtifactStatus(@RequestParam(value="artifactId")String artifactId,
			@RequestParam(value="status")Short status,
			HttpServletRequest request,	HttpServletResponse response){
		Iterable<Integer> artifactIdIt = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
		artifactStatusHandler.updateArtifactStatus(artifactIdIt,status);
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", true);
		outputJson(result,request,response);
	}
	
	@RequestMapping(value="/admin/artifact/bindAwards",method=RequestMethod.POST)
	public void bindArtifactAwards(@RequestParam(value="artifactId")String artifactId,
	        @RequestParam(value="awards")Integer awardsValue,
	        HttpServletRequest request,HttpServletResponse response){
	    
	    Iterable<Integer> artifactIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
	    artifactResultHandler.updateArtifactPrize(artifactIdIter, awardsValue);
	    Map<String,Object> results = Maps.newHashMap();
	    results.put("success", true);
	    outputJson(results,request,response);
	}
	
	
	@RequestMapping(value="/admin/artifact/unbindAwards",method=RequestMethod.POST)
    public void unbindArtifactAwards(@RequestParam(value="artifactId")String artifactId,
            @RequestParam(value="awards")Integer awardsValue,
            HttpServletRequest request,HttpServletResponse response){
	    Iterable<Integer> artifactIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
	    artifactResultHandler.updateArtifactPrize(artifactIdIter, -awardsValue);
        Map<String,Object> results = Maps.newHashMap();
        results.put("success", true);
        outputJson(results,request,response);
    }
	
	
	@RequestMapping(value="/admin/artifact/setHatch",method=RequestMethod.POST)
	public void setArtifactHatch(@RequestParam(value="artifactId")String artifactId,
	        @RequestParam(value="hatch")Short hatchValue,
	        HttpServletRequest request,HttpServletResponse response){
	    Iterable<Integer> artifactIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
	    artifactResultHandler.updateArtifactHatch(artifactIdIter, hatchValue);
	    Map<String,Object> results = Maps.newHashMap();
        results.put("success", true);
        outputJson(results,request,response);
	}
	
	@RequestMapping(value="/admin/artifact/setRound",method=RequestMethod.POST)
	public void setArtifactRound(@RequestParam(value="artifactId")String artifactId,
	        @RequestParam(value="round")Short roundValue,
	        HttpServletRequest request,HttpServletResponse response){
	    Iterable<Integer> artifactIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(artifactId));
	    artifactResultHandler.updateArtifactRound(artifactIdIter, roundValue);
	    Map<String,Object> results = Maps.newHashMap();
        results.put("success", true);
        outputJson(results,request,response);
	    
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
