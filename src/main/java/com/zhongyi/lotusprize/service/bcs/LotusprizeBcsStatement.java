package com.zhongyi.lotusprize.service.bcs;
import java.util.ArrayList;
import java.util.List;

import com.baidu.inf.iis.bcs.policy.Statement;


public class LotusprizeBcsStatement extends Statement {
	
	private List<String> _referer = new ArrayList<String>();

	public List<String> getReferer() {
		return _referer;
	}

	public void setReferer(List<String> referer) {
		this._referer = referer;
	}
	
	public LotusprizeBcsStatement addReferer(String referer){
		_referer.add(referer);
		return this;
	}
	

}
