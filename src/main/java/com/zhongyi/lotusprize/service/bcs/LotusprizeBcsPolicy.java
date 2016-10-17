package com.zhongyi.lotusprize.service.bcs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.inf.iis.bcs.model.Pair;
import com.baidu.inf.iis.bcs.policy.Policy;
import com.baidu.inf.iis.bcs.policy.PolicyAction;
import com.baidu.inf.iis.bcs.policy.Statement;
import com.zhongyi.lotusprize.util.JsonUtil;

public class LotusprizeBcsPolicy extends Policy {

	public String toJson() {
		List<Statement> statements = getStatements();
		if (statements.size() == 0) {
			return "";
		}
		HashMap<String,Object> policyMap = new HashMap<String,Object>();
		List<Map<String,Object>> statementMapList = new ArrayList<Map<String,Object>>();
		policyMap.put("statements", statementMapList);
		for (Statement statement : statements) {
			HashMap<String,Object> statementMap = new HashMap<String,Object>();

			statementMap.put("user", statement.getUser());

			statementMap.put("resource", statement.getResource());

			ArrayList<String> actionList = new ArrayList<String>();
			for (Iterator<PolicyAction> it = statement.getAction().iterator(); it.hasNext();) {
				actionList.add(it.next().name());
			}
			statementMap.put("action", actionList);
			statementMap.put("effect", statement.getEffect().name());
			if ((null != statement.getTime()) && (!statement.getTime().isEmpty())) {
				List<Object> timeList = new ArrayList<Object>();
				timeList.addAll(statement.getTime().getSingleTimeList());
				for (Iterator<Pair<String>> it = statement.getTime().getTimeRangeList().iterator(); it.hasNext();) {
					Pair<String> pair = it.next();
					timeList.add(pair.toArrayList());
				}
				statementMap.put("time", timeList);
			}

			if ((null != statement.getIp()) && (!statement.getIp().isEmpty())) {
				List<Object> ipList = new ArrayList<Object>();
				ipList.addAll(statement.getIp().getSingleIpList());
				ipList.addAll(statement.getIp().getCidrIpList());
				for (Iterator<Pair<String>> it = statement.getIp().getIpRangeList()	.iterator(); it.hasNext();) {
					Pair<String> pair = it.next();
					ipList.add(pair.toArrayList());
				}
				statementMap.put("ip", ipList);
			}
			
			if(statement instanceof LotusprizeBcsStatement){
				LotusprizeBcsStatement lotusprizeBcsStatement = (LotusprizeBcsStatement)statement;
				if(lotusprizeBcsStatement.getReferer()!=null && !lotusprizeBcsStatement.getReferer().isEmpty())
					statementMap.put("referer", lotusprizeBcsStatement.getReferer());
			}
			
			statementMapList.add(statementMap);
		}

		String policyJson = JsonUtil.toJsonString(policyMap);
		System.out.println(policyJson);
		return policyJson;
	}

}
