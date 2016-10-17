package com.zhongyi.lotusprize.mapper.artifact;

import org.apache.ibatis.annotations.Param;

public interface ArtifactVoteMapper {

    int insertArtifactVote(@Param(value="artifactId")Integer artifactId,@Param(value="clientId") String clientId);
}
