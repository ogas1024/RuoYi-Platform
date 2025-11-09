package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Survey;

import java.util.List;
import java.util.Map;

/**
 * 问卷 服务接口
 */
public interface ISurveyService {
    // 管理侧
    /**
     * 列表查询
     */
    List<Survey> list(Survey query);

    /**
     * 详情
     * @param id 问卷ID
     * @param withScopes 是否包含可见范围信息
     */
    Survey detail(Long id, boolean withScopes);

    /**
     * 新增问卷
     */
    int add(Survey survey);

    /**
     * 归档
     */
    int archive(Long id);

    /**
     * 延长截止时间
     */
    int extend(Long id, java.util.Date newDeadline);

    /**
     * 置顶/取消置顶
     */
    int pin(Long id, boolean pinned);

    /**
     * 编辑
     */
    int edit(com.ruoyi.manage.domain.Survey survey);

    /**
     * 发布
     */
    int publish(Long id);

    // 门户
    /**
     * 门户列表
     * @param includeExpired 是否包含已过期
     */
    List<Survey> portalList(Survey query, boolean includeExpired);

    /**
     * 加载我的回答（按题目ID映射）
     */
    Map<Long, Object> loadMyAnswers(Long surveyId, Long userId);

    /**
     * 提交问卷
     * @param answers List<{ questionId, answer(s) }> 格式
     */
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

    /**
     * 管理：生成 AI 汇总报告（基于问卷定义与已提交结果）。
     * @param id 问卷ID
     * @param extraPrompt 额外的提示词（可为空）
     * @return AI 返回的中文报告文本
     */
    String aiSummaryReport(Long id, String extraPrompt);
}
