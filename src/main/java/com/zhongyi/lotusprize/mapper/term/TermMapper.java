package com.zhongyi.lotusprize.mapper.term;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.term.Term;

public interface TermMapper {
	
	int insertTerm(Term term);
	
	List<Term> iterTerm(@Param(value = "baseTermId") int baseTermId, @Param(value = "limit") int limit);
	

}
