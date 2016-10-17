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
import com.zhongyi.lotusprize.service.topic.StageSettingHandler;


@Controller
@RequestMapping(value="/admin/stageSetting")
public class AdminStageSettingController extends BaseController{
    
    @Autowired
    private StageSettingHandler stageSettingHandler;
    
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView showStageSettingPage(){
        ModelAndView mav = new ModelAndView("admin/stageMgr");
        addObjects(mav);
        return mav;
    }
    
    @RequestMapping(value="create",method=RequestMethod.POST)
    public void createStageSetting(@RequestParam(value="stage")Integer stage,
            HttpServletRequest request,HttpServletResponse response){
        
        stageSettingHandler.setStageSetting(stage);
        Map<String,Object> results = Maps.newHashMap();
        results.put("success", true);
        outputJson(results, request, response);
    }
    



}
