package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷-题目 Mapper
 */
public interface SurveyItemMapper {
    List<SurveyItem> selectBySurveyId(@Param("surveyId") Long surveyId);

    int insert(SurveyItem item);

    int deleteBySurveyId(@Param("surveyId") Long surveyId);
}
