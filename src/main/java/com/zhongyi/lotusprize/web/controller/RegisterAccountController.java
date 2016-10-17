package com.zhongyi.lotusprize.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.service.account.RegisterAccountHandler;
import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class RegisterAccountController extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegisterAccountHandler registerHandler;
    
    
    @RequestMapping(value="/account/uniqueEmail")
    public void validateEmailUnique(@RequestParam(value="email") String email,
    		HttpServletRequest request,HttpServletResponse response){
    	Boolean exist = registerHandler.emailExist(email);
    	Map<String,Object> results = Maps.newHashMap();
    	results.put("success", true);
    	results.put("unique",!exist);
    	outputJson(results,request,response);
    }
	
    @RequestMapping(value="/account/register",method=RequestMethod.GET)
    public ModelAndView showRegisterPage(HttpServletRequest request,HttpServletResponse response){
    	String view = WebUtil.localeView(request, "register");
    	ModelAndView mav = new ModelAndView(view);
    	mav.setViewName(view);
    	addFormToken("registerToken",mav);
    	addObjects(mav);
    	return mav;
    }


	@RequestMapping(value="/account/register",method=RequestMethod.POST)
	public void register(@RequestParam(value="fullname")String fullname,
			@RequestParam(value="email")String email,
			@RequestParam(value="password")String password,
			@RequestParam(value="mobile")String mobile,
			@RequestParam(value="address")String address,
			@RequestParam(value="organization")String organization,
			HttpServletRequest request,HttpServletResponse response){

        ErrorCode errorCode = null;
        validateFormToken("registerToken",request);
        String lang = WebUtil.userLang(request);
		try{
            registerHandler.registerUser(fullname,email,password,mobile,address,organization,lang);
        }catch (Exception ex){
            errorCode = ErrorCode.generic_error;
            logger.error("注册异常[fullname:"+fullname+",email:"+email+
            		",mobile:"+mobile+",address:"+address+"]",ex);
        }
        Map<String,Object> results = Maps.newHashMap();
        if(errorCode == null){
            results.put("success",true);
        }else{
            results.put("success",false);
            results.put("errorCode",errorCode);
        }
        outputJson(results,request,response);

	}
	
}
