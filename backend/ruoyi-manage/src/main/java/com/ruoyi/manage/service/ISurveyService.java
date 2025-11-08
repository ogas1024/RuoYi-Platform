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

    /**
     * 门户：带我的回答与（在结束后）选项统计的详情。
     */
    Survey portalDetail(Long id, Long userId);

    /**
     * 门户：我的填写简表（surveyId/title/submitTime）。
     */
    List<Map<String, Object>> selectMyAnswerSummaries(Long userId);

    /**
     * 管理：详情（含选项统计）。
     */
    Survey manageDetailWithStats(Long id);

    /**
     * 管理：某问卷已提交用户列表。
     */
    List<Map<String, Object>> selectSubmitUsers(Long surveyId);

    /**
     * 管理：指定用户的答卷详情（Survey + myAnswers）。
     */
    Survey surveyWithUserAnswers(Long id, Long userId);
}
