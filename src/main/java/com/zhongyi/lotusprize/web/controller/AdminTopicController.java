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

import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.artifact.Artifact2ExpertHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactExpertOperationHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/admin/topic")
public class AdminTopicController extends BaseController {
    
    @Autowired
    private Artifact2ExpertHandler artifact2ExpertHander;
    
    @Autowired
    private TopicRetriveHandler topicRetriveHandler;
    
    @Autowired
    private Artifact2ExpertHandler artifact2ExpertHandler;
    
    @Autowired
    private ArtifactExpertOperationHandler artifactExpertOperationHandler;
    
    @RequestMapping(value = "list")
    public void searchTopic(@RequestParam(value = "searchTitle", required = false) String title,
            @RequestParam(value = "searchCategory",required = false) Short category,
            @RequestParam("orderType") String orderby,
            @RequestParam("order") String ordering,
            DataTablesParameter dtParameter, HttpServletRequest request,
            HttpServletResponse response) {
        
        int offset = dtParameter.getiDisplayStart();
        int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter
                .getiDisplayLength();
        String sqlOrderby = "jiangjin".equalsIgnoreCase(orderby) ? "reward"
                : "artifact_amount";
        String sqlOrdering = "asc".equalsIgnoreCase(ordering) ? "ASC" : "DESC";
        SqlQueryResult<Map<String,Object>> sqlResult =artifact2ExpertHander.listTopic2Expert(category, title, sqlOrderby, sqlOrdering, offset, limit);
        DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                dtParameter.getsEcho(), sqlResult.totalRecords,
                sqlResult.totalRecords, sqlResult.data);
        outputJson(result, request, response);
        
    }

    
    @RequestMapping(value="expert/{expertId}/{topicId}",method=RequestMethod.GET)
    public ModelAndView showTopicArtifactPage(@PathVariable("topicId")Integer topicId,
            @PathVariable("expertId")Integer expertId,
            @RequestParam("round")Short round,
            HttpServletRequest request,HttpServletResponse response){
        String lang = "zh";
        String viewName =  WebUtil.localeView(lang, "admin/showJudgeWork");
        ModelAndView mav = new ModelAndView(viewName);
        Map<String,Object> topicMap = topicRetriveHandler.getTopicAsMap(topicId, lang, false);
        Map<String,Object> expertTopicStatus = artifact2ExpertHandler.getExpertTopicRoundStatus(expertId, topicId,round);
        topicMap.putAll(expertTopicStatus);
        mav.addObject("topic", topicMap);
        mav.addObject("expertId",expertId);
        mav.addObject("round",round);
        addObjects(mav);
        return mav;
    }
    
    @RequestMapping(value="expert/artifact/list",method=RequestMethod.GET)
    public void listArtifact(@RequestParam(value="searchTopic")Integer topicId,
            @RequestParam(value="expertId")Integer expertId,
            @RequestParam(value="round")Short round,
            @RequestParam(value="searchTitle",required=false)String artifactTitle,
            @RequestParam(value="showOption",defaultValue="all")String showOption,
            DataTablesParameter dtParameter, HttpServletRequest request,
            HttpServletResponse response){
        int offset = dtParameter.getiDisplayStart();
        int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter .getiDisplayLength();
        SqlQueryResult<Map<String,Object>> sqlResult = artifactExpertOperationHandler.listByTopic(expertId,topicId,round,artifactTitle,showOption,offset, limit);
        DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                dtParameter.getsEcho(), sqlResult.totalRecords,
                sqlResult.totalRecords, sqlResult.data);
        outputJson(result, request, response);
    }
}
