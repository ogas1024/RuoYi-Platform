package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CrUserScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 积分-用户积分 Mapper
 */
public interface CrUserScoreMapper {
    int upsertScore(@Param("userId") Long userId,
                    @Param("username") String username,
                    @Param("majorId") Long majorId,
                    @Param("delta") Integer delta,
                    @Param("approveInc") Integer approveInc,
                    @Param("bestInc") Integer bestInc,
                    @Param("operator") String operator,
                    @Param("now") java.util.Date now);

    /** 排行榜（可按专业筛选） */
    List<CrUserScore> selectRank(@Param("majorId") Long majorId);
}
