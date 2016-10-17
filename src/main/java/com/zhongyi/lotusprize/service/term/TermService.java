package com.zhongyi.lotusprize.service.term;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.zhongyi.lotusprize.domain.term.Term;
import com.zhongyi.lotusprize.mapper.term.TermMapper;
import com.zhongyi.lotusprize.redis.TermRedis;
import com.zhongyi.lotusprize.service.BaseService;
import com.zhongyi.lotusprize.service.ITransactionOperation;

@Component
public class TermService extends BaseService{
	
	@Autowired
	private TermMapper termMapper;
	
	@Autowired
	private TermRedis termRedis;
	
	public Term term(String termName){
		Integer termId = termRedis.getTermIdByName(termName);
		final Term term = new Term(termName);
		if(termId == null){
			term.setCreateTime(new Date());
			transact(new ITransactionOperation(){
				@Override
				public Object run() {
					termMapper.insertTerm(term);
					termRedis.addTerm(term);
					return null;
				}
				
			});
		}else{
			term.setId(termId);
		}
		return term;
	}
	
	public Collection<Term> terms(String[] termNames){
		List<Integer> termIdList = termRedis.multiGetTermIdByName(termNames);
		final Collection<Term> terms = Sets.newHashSet();
		final Collection<Term> newTerms = Sets.newHashSet();
		final Date createTime = new Date();
		for(int i=0;i<termNames.length;i++){
			Integer termId = termIdList.get(i);
			String termName = termNames[i];
			final Term term = new Term(termName);
			if(termId != null){
				term.setId(termId);
				terms.add(term);
			}else{
				term.setCreateTime(createTime);
				newTerms.add(term);
			}
			
		}
		if(!newTerms.isEmpty()){
			transact(new ITransactionOperation(){
				@Override
				public Void run() {
					for(Term term:newTerms){
						termMapper.insertTerm(term);
					}
					termRedis.multiAddTerm(newTerms.toArray(new Term[0]));
					return null;
				}
				
			});
			terms.addAll(newTerms);
		}
		return terms;
	}

}
