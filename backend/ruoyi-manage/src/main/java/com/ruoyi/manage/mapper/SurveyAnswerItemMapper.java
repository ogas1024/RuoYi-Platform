package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyAnswerItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
/**
 * 问卷-答卷明细 Mapper
 */
public interface SurveyAnswerItemMapper {
    List<SurveyAnswerItem> selectByAnswerId(@Param("answerId") Long answerId);

    int insert(SurveyAnswerItem item);

    /**
     * 统计：按选项ID聚合计数（仅适用于选择题）。
     * 返回的 map 包含 optionId 与 cnt 字段。
     */
    List<Map<String, Object>> countOptionVotes(@Param("surveyId") Long surveyId);

    int deleteByAnswerId(@Param("answerId") Long answerId);

    /**
     * 按问卷ID查询所有文本题的作答（itemId、itemTitle、valueText）。
     */
    List<Map<String, Object>> selectTextAnswersBySurveyId(@Param("surveyId") Long surveyId);
}
