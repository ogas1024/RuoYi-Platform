package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CrUserScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CrUserScoreMapper {
    int upsertScore(@Param("userId") Long userId,
                    @Param("username") String username,
                    @Param("majorId") Long majorId,
                    @Param("delta") Integer delta,
                    @Param("approveInc") Integer approveInc,
                    @Param("bestInc") Integer bestInc,
                    @Param("operator") String operator,
                    @Param("now") java.util.Date now);

    List<CrUserScore> selectRank(@Param("majorId") Long majorId);
}

