package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Survey;

import java.util.List;
import java.util.Map;

public interface ISurveyService {
    // 管理侧
    List<Survey> list(Survey query);

    Survey detail(Long id, boolean withScopes);

    int add(Survey survey);

    int archive(Long id);

    int extend(Long id, java.util.Date newDeadline);

    int pin(Long id, boolean pinned);

    int edit(com.ruoyi.manage.domain.Survey survey);

    int publish(Long id);

    // 门户
    List<Survey> portalList(Survey query, boolean includeExpired);

    Map<Long, Object> loadMyAnswers(Long surveyId, Long userId);

    int submit(Long surveyId, Long userId, java.util.List<Map<String, Object>> answers);
}
