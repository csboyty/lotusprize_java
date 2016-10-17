package com.zhongyi.lotusprize.mapper.artifact;

import org.apache.ibatis.annotations.Param;


public interface ArtifactResultMapper {
	
	int insertArtifactResult(Integer artifactId);
	
	int updateVote(@Param("artifactId")Integer artifactId,@Param("voteValue")Integer voteValue);
	
	int updatePrize(@Param("artifactId")Integer artifactId,@Param("awardsValue")Integer awardsValue);
	
	int updateHatch(@Param("artifactId")Integer artifactId,@Param("hatchValue")Short hatchValue);
	
	int updateRound(@Param("artifactId")Integer artifactId,@Param("roundValue")Short roundValue);
	
	
	
	
	
	
}
