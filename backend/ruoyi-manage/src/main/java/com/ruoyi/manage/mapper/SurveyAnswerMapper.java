package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 问卷-答卷 Mapper
 */
public interface SurveyAnswerMapper {
    SurveyAnswer selectOne(@Param("surveyId") Long surveyId, @Param("userId") Long userId);

    /** 插入（存在则忽略，用于幂等） */
    int insertIgnore(SurveyAnswer ans);

    int updateById(SurveyAnswer ans);

    /** 我的答卷列表 */
    List<SurveyAnswer> selectMyAnswers(@Param("userId") Long userId);

    /** 已提交用户列表（管理端展示） */
    List<Map<String, Object>> selectSubmitUsers(@Param("surveyId") Long surveyId);
}
