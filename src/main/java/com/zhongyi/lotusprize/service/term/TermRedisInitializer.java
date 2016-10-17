package com.zhongyi.lotusprize.service.term;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.zhongyi.lotusprize.domain.term.Term;
import com.zhongyi.lotusprize.mapper.term.TermMapper;
import com.zhongyi.lotusprize.redis.TermRedis;

public class TermRedisInitializer {
	
	@Autowired
	private TermMapper termMapper;
	
	@Autowired
	private TermRedis termRedis;
	
	@PostConstruct
	public void init() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				TermIterator termIt = new TermIterator(0,20);
				while(termIt.hasNext()){
					Term term = termIt.next();
					termRedis.addTerm(term);
				}
			}
		}).start();
	}
	
	
	private class TermIterator implements Iterator<Term>{
		
		private final int limit;
		private int offsetTermId;
		private Iterator<Term> _iterator;
		
		private TermIterator(int offsetTermId,int limit){
			this.offsetTermId = offsetTermId;
			this.limit = limit;
			_iterator = termMapper.iterTerm(offsetTermId, limit).iterator();
		}

		@Override
		public boolean hasNext() {
			boolean _hasNext = _iterator.hasNext();
			if(!_hasNext){
				_iterator = termMapper.iterTerm(offsetTermId,limit).iterator();
				_hasNext = _iterator.hasNext();
			}
			return _hasNext;
		}

		@Override
		public Term next() {
			Term term = _iterator.next();
			offsetTermId = term.getId();
			return term;
		}

		@Override
		public void remove() {
			
		}
		
	}

}
