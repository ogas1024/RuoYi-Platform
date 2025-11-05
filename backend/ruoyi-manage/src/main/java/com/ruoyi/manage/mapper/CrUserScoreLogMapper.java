package com.ruoyi.manage.mapper;

import org.apache.ibatis.annotations.Param;

public interface CrUserScoreLogMapper {
    /**
     * 插入流水（若已存在则忽略），返回受影响行数 1=首次发放，0=重复忽略
     */
    int insertApproveLogIfAbsent(@Param("userId") Long userId,
                                 @Param("username") String username,
                                 @Param("majorId") Long majorId,
                                 @Param("resourceId") Long resourceId,
                                 @Param("delta") Integer delta,
                                 @Param("operator") String operator,
                                 @Param("now") java.util.Date now);

    int insertBestLogIfAbsent(@Param("userId") Long userId,
                               @Param("username") String username,
                               @Param("majorId") Long majorId,
                               @Param("resourceId") Long resourceId,
                               @Param("delta") Integer delta,
                               @Param("operator") String operator,
                               @Param("now") java.util.Date now);
}

