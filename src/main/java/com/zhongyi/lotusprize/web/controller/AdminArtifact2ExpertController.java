package com.zhongyi.lotusprize.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.artifact.Artifact2ExpertHandler;
import com.zhongyi.lotusprize.util.Transforms;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/admin/artifact2expert")
public class AdminArtifact2ExpertController extends BaseController {
	
	@Autowired
	private Artifact2ExpertHandler artifact2ExpertHandler;
	
	
	@RequestMapping(value="topic2expert",method=RequestMethod.GET)
	public void listTopic2Expert( @RequestParam(value = "searchTitle", required = false) String title,
            @RequestParam(value = "searchCategory",required = false) Short category,
            @RequestParam("orderType") String orderby,
            @RequestParam("order") String ordering,
	        DataTablesParameter dtParameter,
            HttpServletRequest request,HttpServletResponse response){
	    
        int offset = dtParameter.getiDisplayStart();
        int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter
                .getiDisplayLength();
        String sqlOrderby = "jiangjin".equalsIgnoreCase(orderby) ? "reward" : "artifact_amount";
        String sqlOrdering = "asc".equalsIgnoreCase(ordering) ? "ASC" : "DESC";
        SqlQueryResult<Map<String,Object>> sqlResult = artifact2ExpertHandler.listTopic2Expert(category, title, sqlOrderby, sqlOrdering, offset, limit);
        DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                dtParameter.getsEcho(), sqlResult.totalRecords,
                sqlResult.totalRecords, sqlResult.data);
        outputJson(result, request, response);
	}
	

	@RequestMapping(value="expertStatus",method=RequestMethod.GET)
	public void  listExpertTopicStatus(@RequestParam(value="expertId")Integer expertId,
	        @RequestParam(value="round")Short round,
            DataTablesParameter dtParameter,
            HttpServletRequest request,HttpServletResponse response){
	    
	    int offset = dtParameter.getiDisplayStart();
        int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter.getiDisplayLength();
        SqlQueryResult<Map<String,Object>> sqlResult = artifact2ExpertHandler.listExpertTopicStatus(expertId, round, offset, limit);
        DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                dtParameter.getsEcho(), sqlResult.totalRecords,
                sqlResult.totalRecords, sqlResult.data);
        outputJson(result, request, response);
	}
	
	
	
	@RequestMapping(value="bind",method=RequestMethod.POST)
	public void bindTopic2Expert(@RequestParam(value="topicId")String topicId,
	        @RequestParam(value="expertId")String expertId,
	        @RequestParam(value="round")Short round,
	        HttpServletRequest request,HttpServletResponse response){
	    
	    Iterable<Integer> topicIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(topicId));
        Iterable<Integer> expertIdIter = Transforms.toIntegerIter(Transforms.spliteByComma(expertId));
        
        if(round == 1){
            artifact2ExpertHandler.createTopic2ExpertRound1(topicIdIter,expertIdIter);
        }else if(round == 2){
            artifact2ExpertHandler.createTopic2ExpertRound2(topicIdIter, expertIdIter);
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("success", true);
        outputJson(result,request,response);
	}
	
	
	@RequestMapping(value="artifactScores",method=RequestMethod.GET)
	public void listArtifactScore(@RequestParam(value="artifactId")Integer artifactId,
	        @RequestParam(value="round")Short round,
	        HttpServletRequest request,HttpServletResponse response){
	    
	    List<Map<String,Object>> scores= artifact2ExpertHandler.artifactScores(artifactId, round);
	    Map<String,Object> result = Maps.newHashMap();
	    result.put("scores",scores);
	    result.put("success", true);
        outputJson(result,request,response);
	}
	
	

}
