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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.account.AccountOperationHandler;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/admin/user")
public class AdminUserManageController extends BaseController{
	
	@Autowired
	private AccountOperationHandler accountOperationHandler;
	
	private final Function<Account,Map<String,Object>> accountToMapFuncation = new AccountMapFunction();
	
	@RequestMapping(value="userManage",method=RequestMethod.GET)
	public ModelAndView showAdminUserManagePage(HttpServletRequest request,HttpServletResponse response){
		String view = WebUtil.localeView(request, "admin/userMgr");
		ModelAndView mav = new ModelAndView(view);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="info",method=RequestMethod.GET)
	public void getUserInfo(@RequestParam(value="id",required=false)Integer accountId,
	        HttpServletRequest request,HttpServletResponse response){
	    Account account = accountOperationHandler.accountById(accountId);
	    outputJson(account, request, response);
	}
	
	@RequestMapping(value="list")
	public void listUser(@RequestParam(value="fullname",required=false) String fullnameOrEmail,
			@RequestParam(value="role",required=false) String role,
			DataTablesParameter dtParameter, HttpServletRequest request,
			HttpServletResponse response){
		int offset = dtParameter.getiDisplayStart();
		int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter
				.getiDisplayLength();
		String searchString = null;
		if(!Strings.isNullOrEmpty(fullnameOrEmail)){
		    searchString = fullnameOrEmail + "%";
		}
		SqlQueryResult<Account> sqlResult = accountOperationHandler.listBy(searchString,role,offset, limit);
		DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
				dtParameter.getsEcho(), sqlResult.totalRecords,
				sqlResult.totalRecords, Lists.transform(sqlResult.data,accountToMapFuncation));
		outputJson(result, request, response);
		
	}
	
	@RequestMapping(value="createTopicManager",method=RequestMethod.GET)
	public ModelAndView showCreateTopicManagerPage(	HttpServletRequest request,HttpServletResponse response){
		String view = WebUtil.localeView(request, "admin/corpAdminCreate");
		ModelAndView mav = new ModelAndView(view);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="update/{accountId}",method=RequestMethod.GET)
	public ModelAndView showUpdateUserPage(	@PathVariable("accountId")Integer accountId,
			HttpServletRequest request,HttpServletResponse response){
		String view = WebUtil.localeView(request, "admin/userInfoChange");
		ModelAndView mav = new ModelAndView(view);
		Account user = accountOperationHandler.accountById(accountId);
		mav.addObject("user", accountToMapFuncation.apply(user));
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="update",method=RequestMethod.POST)
	public void updateUser(@RequestParam(value="id",required=false)Integer accountId,
			@RequestParam(value="fullname")String fullname,
			@RequestParam(value="email")String email,
			@RequestParam(value="mobile")String mobile,
			@RequestParam(value="address")String address,
			@RequestParam(value="memo",required=false)String memo,
			@RequestParam(value="organization",required=false)String organization,
			HttpServletRequest request,HttpServletResponse response){
		accountOperationHandler.createOrUpdateAccount(accountId,fullname, email,mobile,
				address, memo, organization,null);
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}

	@RequestMapping(value="remove",method=RequestMethod.POST)
	public void removeUser(@RequestParam(value="id")Integer accountId,
			HttpServletRequest request,HttpServletResponse response){
		
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
			
	
	
	@RequestMapping(value="createTopicManager",method=RequestMethod.POST)
	public void createTopicManager(
			@RequestParam(value="id",required=false)Integer accountId,
			@RequestParam(value="fullname")String fullname,
			@RequestParam(value="email")String email,
			@RequestParam(value="mobile")String mobile,
			@RequestParam(value="address")String address,
			@RequestParam(value="memo",required=false)String memo,
			@RequestParam(value="organization",required=false)String organization,
			HttpServletRequest request,HttpServletResponse response){
		accountOperationHandler.createOrUpdateAccount(accountId,fullname, email,mobile,
				address, memo, organization, Roles.topicManagerRole);
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}

	@RequestMapping(value="createExpert",method=RequestMethod.GET)
	public ModelAndView showCreateExpertPage(HttpServletRequest request,HttpServletResponse response){
	    String view = WebUtil.localeView(request, "admin/judgeCreate");
        ModelAndView mav = new ModelAndView(view);
        addObjects(mav);
        return mav;
	}
	
	@RequestMapping(value="createExpert",method=RequestMethod.POST)
	public void createExpert(@RequestParam(value="id",required=false)Integer accountId,
			@RequestParam(value="fullname")String fullname,
			@RequestParam(value="email")String email,
			@RequestParam(value="mobile")String mobile,
			@RequestParam(value="address")String address,
			@RequestParam(value="memo",required=false)String memo,
			@RequestParam(value="organization",required=false)String organization,
			HttpServletRequest request,HttpServletResponse response){
		accountOperationHandler.createOrUpdateAccount(accountId,fullname, email,mobile, 
				address, memo, organization, Roles.expertRole);
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	
	@RequestMapping(value="resetPassword/{accountId}",method=RequestMethod.GET)
	public ModelAndView showResetPasswordPage(@PathVariable("accountId")Integer accountId,
			HttpServletRequest request,HttpServletResponse response){
		String view = WebUtil.localeView(request, "admin/userPwdReset");
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("id", accountId);
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value="resetPassword",method=RequestMethod.POST)
	public void resetPassword(@RequestParam(value="id")Integer accountId,
			@RequestParam(value="password")String password,
			HttpServletRequest request,HttpServletResponse response){
		accountOperationHandler.resetPasswordForce(accountId, password);
		Map<String,Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	
	
	public static class AccountMapFunction implements Function<Account,Map<String,Object>>{
		@Override
		public Map<String, Object> apply(Account account) {
			Map<String, Object> map = Maps.newLinkedHashMap();
			map.put("id", account.getId());
			map.put("fullname", account.getFullname());
			map.put("email", account.getEmail());
			map.put("address", account.getAddress());
			map.put("mobile", account.getMobile());
			map.put("roleName", Roles.roleNames(account.getRoleValue()));
			if(account.getAccountProfile()!= null){
				map.put("organization", account.getAccountProfile().getOrganization());
				map.put("memo", account.getAccountProfile().getMemo());
				map.put("gender", account.getAccountProfile().getGender());
			}else{
				map.put("organization", null);
				map.put("memo", null);
				map.put("gender", 1);
			}
			return map;
		}
		
	}
}
