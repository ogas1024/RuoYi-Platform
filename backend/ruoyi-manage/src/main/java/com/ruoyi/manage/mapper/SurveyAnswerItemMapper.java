package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.SurveyAnswerItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SurveyAnswerItemMapper {
    List<SurveyAnswerItem> selectByAnswerId(@Param("answerId") Long answerId);

    int insert(SurveyAnswerItem item);

    int deleteByAnswerId(@Param("answerId") Long answerId);
}

