package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveyAnswerMapper {
    SurveyAnswer selectOne(@Param("surveyId") Long surveyId, @Param("userId") Long userId);

    int insertIgnore(SurveyAnswer ans);

    int updateById(SurveyAnswer ans);

    List<SurveyAnswer> selectMyAnswers(@Param("userId") Long userId);

    java.util.List<java.util.Map<String, Object>> selectSubmitUsers(@Param("surveyId") Long surveyId);
}
