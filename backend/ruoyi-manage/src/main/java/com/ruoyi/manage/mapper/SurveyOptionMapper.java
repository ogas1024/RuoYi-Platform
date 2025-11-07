package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyOption;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveyOptionMapper {
    List<SurveyOption> selectByItemId(@Param("itemId") Long itemId);

    List<SurveyOption> selectBySurveyId(@Param("surveyId") Long surveyId);

    int insert(SurveyOption opt);

    int deleteBySurveyId(@Param("surveyId") Long surveyId);
}

