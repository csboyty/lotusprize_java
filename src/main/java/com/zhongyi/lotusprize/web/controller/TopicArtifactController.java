package com.zhongyi.lotusprize.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.artifact.ArtifactResultHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.Transforms;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/topic/artifact")
public class TopicArtifactController extends BaseController {
    
    @Autowired
    private ArtifactResultHandler artifactResultHandler;
    
    @Autowired
    private ArtifactRetriveHandler artifactRetriveHandler;
    
    @Autowired
    private TopicRetriveHandler topicRetriveHandler;
    
    
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView shoVotewArtifactPage(@RequestParam("topicId") Integer topicId,
            HttpServletRequest request,
            HttpServletResponse response){
        String lang = WebUtil.userLang(request);
        String viewName = WebUtil.localeView(lang, "user/showWork");
        ModelAndView mav = new ModelAndView(viewName);
        Map<String,Object> topicMap = topicRetriveHandler.getTopicAsMap(topicId, lang, false);
        mav.addObject("topic", topicMap);
        addObjects(mav);
        return mav;
    }
    
    
    @RequestMapping(value="list",method=RequestMethod.POST)
    public void listVoteArtifact(@RequestParam(value = "searchTopic",required = false) Integer topicId,
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
            SqlQueryResult<Map<String,Object>> sqlResult = artifactRetriveHandler.listByAdminView(topicId, title, statusIt,
                    sqlOrderby, sqlOrdering, offset, limit);
            DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
                    dtParameter.getsEcho(), sqlResult.totalRecords,
                    sqlResult.totalRecords, sqlResult.data);
            outputJson(result, request, response);
    }
    
    @RequestMapping(value="vote",method=RequestMethod.POST)
    public void doVote(@RequestParam(value="artifactId")Integer artifactId,
            @RequestParam(value="clientId")String clientId,
            HttpServletRequest request,HttpServletResponse response){
        
        validateFormToken("voteToken",request);
        artifactResultHandler.updateArtifactVote(artifactId, clientId);
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
