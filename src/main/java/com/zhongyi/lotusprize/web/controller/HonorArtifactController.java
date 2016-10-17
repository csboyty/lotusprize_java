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
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.WebUtil;


@Controller
@RequestMapping(value="/gallery")
public class HonorArtifactController  extends BaseController{
    
    @Autowired
    private ArtifactRetriveHandler artifactRetriveHandler;
    
    @Autowired
    private TopicRetriveHandler topicRetriveHandler;

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView showGalleryPage(@RequestParam(value="artifactId",required=false)Integer artifactId,
            @RequestParam(value="award",required=false) String award,
            HttpServletRequest request,HttpServletResponse response){
        
        String lang = WebUtil.userLang(request);
        String viewName = WebUtil.localeView(lang, "user/showAwardsWork");
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("artifactId", artifactId);
        mav.addObject("award",award);
        mav.addObject("topicNames", topicRetriveHandler.topicId2Names(lang));
        addObjects(mav);
        return mav;
        
    }
    
    @RequestMapping(value="list",method=RequestMethod.GET)
    public void listHonorArtifact(@RequestParam(value="topicId",required=false)Integer topicId,
            HttpServletRequest request,HttpServletResponse response){
        List<Map<String,Object>> artifactList ;
        if(topicId != null){
            artifactList = artifactRetriveHandler.topicHonorArtifact(topicId);
        }else{
            artifactList = artifactRetriveHandler.honorArtifact();
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("success", true);
        result.put("artifacts", artifactList);
        outputJson(result,request,response);
    }
    
    
}
