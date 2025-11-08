package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyAnswerItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveyAnswerItemMapper {
    List<SurveyAnswerItem> selectByAnswerId(@Param("answerId") Long answerId);

    int insert(SurveyAnswerItem item);

    /**
     * 统计：按选项ID聚合计数（仅适用于选择题）。
     * 返回的 map 包含 optionId 与 cnt 字段。
     */
    List<java.util.Map<String, Object>> countOptionVotes(@Param("surveyId") Long surveyId);

    int deleteByAnswerId(@Param("answerId") Long answerId);
}
