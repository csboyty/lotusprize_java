package com.zhongyi.lotusprize.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.io.Closeables;
import com.zhongyi.lotusprize.exception.LotusprizeError;
import com.zhongyi.lotusprize.service.ErrorCode;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.util.WebUtil.WebRequestType;


@ControllerAdvice
public class GlobalExceptionHandler {
	private final  Logger logger = LoggerFactory.getLogger(getClass());
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public Object handleDBConflict(DataIntegrityViolationException ex,HttpServletRequest request, HttpServletResponse response){
		logger.error("数据库错误",ex);
		return handleException(new LotusprizeError(ErrorCode.db_conflict,ex),request,response);
	}
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("success", false);
        if(ex instanceof LotusprizeError){
        	results.put("errorCode", ((LotusprizeError)ex).errorCode());
        }else{
        	results.put("errorCode",ErrorCode.generic_error);
        	logger.error("",ex);
        }
        results.put("errorMessage", ex.getMessage());
        if(WebUtil.requestType(request) == WebRequestType.ajax || request.getRequestURI().indexOf("/s/upload")!=-1){
        	ajax50xError(results,request,response);
        	return null;
        }else{
        	return noAjax50xError(results,request,response);
        }
       
    }
    
    
    private void ajax50xError(Map<String,Object> results, HttpServletRequest request, HttpServletResponse response){
    	 String contentType = "application/json;charset=UTF-8";
         if (WebUtil.isIE(request)) {
             contentType = "text/plain;charset=UTF-8";
         }
         response.setContentType(contentType);
         response.setHeader("Cache-Control", "no-store, no-cache");
         response.setHeader("Pragma", "no-cache");
         response.setDateHeader("Expires", 0);
         OutputStream out = null;
         try {
             out = response.getOutputStream();
             JsonUtil.toJson(results, out);
             out.flush();
         } catch (Exception e) {

         } finally {
             try {
 				Closeables.close(out, true);
 			} catch (IOException e) {
 			}
         }
    }
    
    private ModelAndView noAjax50xError(Map<String,Object> results, HttpServletRequest request, HttpServletResponse response){
    	ModelAndView mav = new ModelAndView();
    	mav.addAllObjects(results);
		mav.addObject("url", request.getRequestURI());
		mav.addObject("timestamp", new Date());
		mav.addObject("status", 500);
    	mav.setViewName("50x");
    	return mav;
    }
    
    

}

