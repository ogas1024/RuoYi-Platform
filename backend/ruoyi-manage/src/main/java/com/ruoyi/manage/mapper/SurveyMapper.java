package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Survey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 问卷 Mapper
 */
public interface SurveyMapper {
    /** 列表查询（含置顶过期处理） */
    List<Survey> selectList(@Param("q") Survey query,
                            @Param("userId") Long userId,
                            @Param("isAdmin") Integer isAdmin);

    Survey selectById(@Param("id") Long id);

    int insert(Survey survey);

    int update(Survey survey);

    /** 归档 */
    int archive(@Param("id") Long id);

    /** 延长截止时间 */
    int extend(@Param("id") Long id, @Param("deadline") Date deadline);

    /** 置顶 */
    int pin(@Param("id") Long id);

    /** 取消置顶 */
    int unpin(@Param("id") Long id);

    /** 自动取消过期置顶 */
    int unpinExpired();
}
