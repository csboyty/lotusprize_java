package com.zhongyi.lotusprize.mapper.term;

import org.apache.ibatis.annotations.Param;

import com.zhongyi.lotusprize.domain.term.Artifact2Term;

public interface Artifact2TermMapper {
	
	int insertArtifact2Term(Artifact2Term artifact2Term);
	
	int deleteByArtifactIdAndTermId(@Param("artifactId")Integer artifactId,@Param("termId")Integer termId);

}
