package com.zhongyi.lotusprize.web.controller;

import java.io.IOException;
import java.util.Collection;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.domain.account.Account;
import com.zhongyi.lotusprize.domain.topic.Topic;
import com.zhongyi.lotusprize.domain.topic.TopicDetail;
import com.zhongyi.lotusprize.domain.topic.TopicIntroduce;
import com.zhongyi.lotusprize.mapper.SqlQueryResult;
import com.zhongyi.lotusprize.service.account.AccountOperationHandler;
import com.zhongyi.lotusprize.service.artifact.ArtifactRetriveHandler;
import com.zhongyi.lotusprize.service.role.Roles;
import com.zhongyi.lotusprize.service.topic.StageSettingHandler;
import com.zhongyi.lotusprize.service.topic.TopicOperationHandler;
import com.zhongyi.lotusprize.service.topic.TopicRetriveHandler;
import com.zhongyi.lotusprize.util.JsonUtil;
import com.zhongyi.lotusprize.util.WebUtil;
import com.zhongyi.lotusprize.web.misc.DataTablesParameter;
import com.zhongyi.lotusprize.web.misc.DataTablesResult;

@Controller
@RequestMapping(value="/topic")
public class TopicController extends BaseController {

	@Autowired
	private TopicOperationHandler topicOperationHandler;
	
	@Autowired
	private TopicRetriveHandler topicRetriveHandler;
	
	@Autowired
	private AccountOperationHandler accountOperationHandler;
	
	@Autowired
    private ArtifactRetriveHandler artifactRetriveHandler;
	
	
	private final Function<Account,Map<String,Object>> topicManagerMapFunction = new TopicManagerMapFunction();

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public ModelAndView createTopicPage() {
		ModelAndView mav = new ModelAndView("admin/topicCreate");
		mav.addObject("topicManagers",Collections2.transform(accountOperationHandler.topicManagers(),topicManagerMapFunction));
		addObjects(mav);
		return mav;
	}

	@RequestMapping(value = "update/{topicId}", method = RequestMethod.GET)
	public ModelAndView topicDetailPage(@PathVariable("topicId") Integer topicId,
			@RequestParam("lang") String lang,
			HttpServletRequest request,HttpServletResponse response) throws IOException {
		Map<String,Object> topicWithDetailMap = topicOperationHandler.getTopicAsMap(topicId,lang,false);
		if (topicWithDetailMap == null || topicWithDetailMap.isEmpty()) {
			response.sendError(404);
			return null;
		}
		ModelAndView mav = new ModelAndView("admin/topicCreate");
		mav.addObject("topic", topicWithDetailMap);
		mav.addObject("lang", lang);
		mav.addObject("topicManagers",Collections2.transform(accountOperationHandler.topicManagers(),topicManagerMapFunction));
		addObjects(mav);
		return mav;
	}
	
	@RequestMapping(value = "mgr", method = RequestMethod.GET)
	public ModelAndView topicListPage() {
		ModelAndView mav = new ModelAndView("admin/topicMgr");
		addObjects(mav);
		return mav;
	}

	@RequestMapping(value = "view", method = RequestMethod.GET)
	public ModelAndView viewTopics(HttpServletRequest request) {
		String view = WebUtil.localeView(request, "user/topicView");
		ModelAndView mav = new ModelAndView(view);
		addObjects(mav);
		return mav;
	}

	@RequestMapping(value = "view/{topicId}", method = RequestMethod.GET)
	public ModelAndView viewTopic(@PathVariable("topicId") Integer topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String lang = WebUtil.userLang(request);
		Map<String,Object> topicWithDetailMap = topicOperationHandler.getTopicAsMap(topicId,lang,false);
		if (topicWithDetailMap == null || topicWithDetailMap.isEmpty()) {
			response.sendError(404);
			return null;
		}
		Integer artifactPassAmount = artifactRetriveHandler.countArtifactByTopicAndStatus(topicId, Lists.newArrayList((int)StageSettingHandler.stage_round1,(int)StageSettingHandler.stage_pass));
		topicWithDetailMap.put("artifactPassAmount", artifactPassAmount);
		String view = WebUtil.localeView(request, "user/topicDetail");
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("topic", topicWithDetailMap);
		addObjects(mav);
		return mav;
	}
	
	
	@RequestMapping(value = "view/{topicId}/introduce", method = RequestMethod.GET)
	public void topicIntroduce(
			@PathVariable("topicId") Integer topicId,
			@RequestParam("lang") String lang,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> topicWithDetailMap = topicOperationHandler.getTopicAsMap(topicId,lang,false);
		Collection<Map<String,Object>> introduces =(Collection<Map<String,Object>>)topicWithDetailMap.get("topicIntroduces");
		outputJson(introduces,request,response);
	}

	@RequestMapping(value = "createOrUpdate", method = RequestMethod.POST)
	public void saveTopic(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "topicSettingId") Integer topicSettingId,
			@RequestParam(value = "ownAccountId") Integer ownAccountId,
			@RequestParam(value = "lang") String lang,
			@RequestParam(value = "category") Short category,
			@RequestParam(value = "name") String title,
			@RequestParam(value = "description") String description,
			@RequestParam(value = "corpName") String corp,
			@RequestParam(value = "corpLogo") String corpLogo,
			@RequestParam(value = "reward") Double reward,
			@RequestParam(value = "video",required = false) String video,
			@RequestParam(value = "profile",required = false) String profile,
			@RequestParam(value = "addition", required = false) String addition,
			@RequestParam(value = "attachment", required = false) String attachment,
			@RequestParam(value = "introduce") String introductJson,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Topic topic = new Topic();
		topic.setId(id);
		topic.setOwnAccountId(ownAccountId);
		topic.setCorpLogo(corpLogo);
		topic.setReward(reward);
		topic.setVideo(video);
		topic.setProfile(profile);
		topic.setCategory(category);

		TopicDetail topicDetail = new TopicDetail();
		topicDetail.setTopicSettingId(topicSettingId);
		topicDetail.setLang(lang);
		topicDetail.setTitle(title);
		topicDetail.setDescription(description);
		topicDetail.setCorp(corp);
		topicDetail.setAddition(Strings.isNullOrEmpty(addition)?null:addition);
		topicDetail.setAttachment(attachment);
		
		Collection<TopicIntroduce> topicIntroduces = parseTopicIntroduceJson(introductJson);
		topicDetail.setTopicIntroduces(topicIntroduces);
		
		topicOperationHandler.createOrUpdateTopic(topic,topicDetail);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}


	@RequestMapping(value = "list")
	public void searchTopic(
			@RequestParam(value = "searchTitle", required = false) String title,
			@RequestParam(value = "searchCategory",required = false) Short category,
			@RequestParam("orderType") String orderby,
			@RequestParam("order") String ordering,
			@RequestParam(value="lang",required=false)String paramLang,
			DataTablesParameter dtParameter, HttpServletRequest request,
			HttpServletResponse response) {
		
		boolean isAdmin =false;
		Integer roleValue = WebUtil.currentUserRoleValue();
		if(Roles.isRole(roleValue, Roles.adminRole)){
			isAdmin = true;
		}
		
		String lang = Strings.isNullOrEmpty(paramLang) ? "zh":paramLang;
		int offset = dtParameter.getiDisplayStart();
		int limit = dtParameter.getiDisplayLength() == 0 ? 10 : dtParameter
				.getiDisplayLength();
		String sqlOrderby = "jiangjin".equalsIgnoreCase(orderby) ? "reward"
				: "artifact_amount";
		String sqlOrdering = "asc".equalsIgnoreCase(ordering) ? "ASC" : "DESC";
		SqlQueryResult<Map<String,Object>> sqlResult = topicRetriveHandler.listBy(category,title,lang,sqlOrderby, sqlOrdering, offset, limit,isAdmin);
		DataTablesResult<Map<String,Object>> result = new DataTablesResult<Map<String,Object>>(
				dtParameter.getsEcho(), sqlResult.totalRecords,
				sqlResult.totalRecords, sqlResult.data);
		outputJson(result, request, response);
	}

	@RequestMapping(value = "remove", method = RequestMethod.POST)
	public void remove(@RequestParam(value = "id") Integer topicId,
			HttpServletRequest request, HttpServletResponse response) {
		topicOperationHandler.removeTopic(topicId);
		Map<String, Object> results = Maps.newHashMap();
		results.put("success", true);
		outputJson(results, request, response);
	}
	
	

	private Collection<TopicIntroduce> parseTopicIntroduceJson(String introduceJson){
		Map<Integer,Map<String,String>> introduceMap = JsonUtil.fromJson(new TypeReference<Map<Integer,Map<String,String>>>(){},introduceJson);
		Collection<TopicIntroduce> topicIntroduces = Lists.newArrayListWithCapacity(introduceMap.size());
		for(Map.Entry<Integer, Map<String,String>> entry:introduceMap.entrySet()){
			TopicIntroduce topicIntroduce = new TopicIntroduce();
			topicIntroduce.setPos(entry.getKey());
			topicIntroduce.setText(entry.getValue().get("text"));
			topicIntroduce.setImage(entry.getValue().get("image"));
			topicIntroduces.add(topicIntroduce);
		}
		return topicIntroduces;
	}
	
	private static class TopicManagerMapFunction implements Function<Account,Map<String,Object>>{

		@Override
		public Map<String, Object> apply(Account input) {
			Map<String,Object> map = Maps.newHashMap();
			map.put("id", input.getId());
			map.put("organization", input.getAccountProfile() == null ? "":input.getAccountProfile().getOrganization());
			return map;
		}
		
	}
	
	

}
