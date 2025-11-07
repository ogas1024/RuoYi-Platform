package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Survey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SurveyMapper {
    List<Survey> selectList(@Param("q") Survey query,
                            @Param("userId") Long userId,
                            @Param("isAdmin") Integer isAdmin);

    Survey selectById(@Param("id") Long id);

    int insert(Survey survey);

    int update(Survey survey);

    int archive(@Param("id") Long id);

    int extend(@Param("id") Long id, @Param("deadline") Date deadline);

    int pin(@Param("id") Long id);

    int unpin(@Param("id") Long id);

    int unpinExpired();
}
