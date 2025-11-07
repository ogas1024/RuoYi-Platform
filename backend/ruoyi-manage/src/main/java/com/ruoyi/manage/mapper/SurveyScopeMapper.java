package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyScope;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveyScopeMapper {
    List<SurveyScope> selectBySurveyId(@Param("surveyId") Long surveyId);

    int insert(SurveyScope scope);

    int deleteBySurveyId(@Param("surveyId") Long surveyId);
}

