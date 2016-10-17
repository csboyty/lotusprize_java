package com.zhongyi.lotusprize.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zhongyi.lotusprize.util.WebUtil;

@Controller
public class HomeController extends BaseController{
	
	@RequestMapping(value="/admin",method=RequestMethod.GET)
	public String adminHome(HttpServletRequest request,HttpServletResponse response,
			RedirectAttributes redirectAttrs){
		String lang = WebUtil.userLang(request);
		redirectAttrs.addAttribute("_lang", lang);
		return "redirect:/s/topic/mgr";
	}
	
	@RequestMapping(value="/expert",method=RequestMethod.GET)
	public String expertHome(HttpServletRequest request,HttpServletResponse response,
			RedirectAttributes redirectAttrs){
		String lang = WebUtil.userLang(request);
		redirectAttrs.addAttribute("_lang", lang);
		return "redirect:/s/expert/home";
	}
	
	@RequestMapping(value="/topicManager",method=RequestMethod.GET)
	public String topicManagerHome(HttpServletRequest request,HttpServletResponse response,
			RedirectAttributes redirectAttrs){
		String lang = WebUtil.userLang(request);
		redirectAttrs.addAttribute("_lang", lang);
		return "redirect:/s/topicManager/home";
		
	}
	
	@RequestMapping(value="/user",method=RequestMethod.GET)
	public String userHome(HttpServletRequest request,HttpServletResponse response,
			RedirectAttributes redirectAttrs){
	    
		String lang = WebUtil.userLang(request);
		redirectAttrs.addAttribute("_lang", lang);
		return "redirect:/s/user/artifact";
	}

}
