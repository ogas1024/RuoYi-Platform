package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.Survey;
import com.ruoyi.manage.domain.SurveyAnswer;
import com.ruoyi.manage.service.ISurveyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/portal/survey")
public class PortalSurveyController extends BaseController {

    @Resource
    private ISurveyService service;
    @Resource
    private com.ruoyi.manage.mapper.SurveyAnswerMapper answerMapper;
    @Resource
    private com.ruoyi.manage.mapper.SurveyMapper surveyMapper;
    @Resource
    private com.ruoyi.manage.mapper.SurveyAnswerItemMapper answerItemMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(Survey query) {
        startPage();
        // 门户仅展示已发布；包含未过期与已过期
        if (query == null) query = new Survey();
        query.setStatus(1);
        List<Survey> list = service.portalList(query, true);
        return getDataTable(list);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        Survey s = service.detail(id, false);
        // 门户详情：允许已发布(1)与已归档(2)查看；删除或草稿均不可见
        if (s == null || s.getStatus() == null || (s.getStatus() != 1 && s.getStatus() != 2)) {
            return error("问卷不存在或未发布");
        }
        Map<Long, Object> my = service.loadMyAnswers(id, getUserId());
        s.setMyAnswers(my);
        // 门户：仅在“投票结束”（过期或归档）后返回选项统计。
        boolean ended = (s.getDeadline() != null && new Date().after(s.getDeadline())) || (s.getStatus() != null && s.getStatus() == 2);
        if (ended) {
            try {
                java.util.List<java.util.Map<String, Object>> rows = answerMapper2().countOptionVotes(id);
                java.util.Map<Long, Integer> cntMap = new java.util.HashMap<>();
                for (java.util.Map<String, Object> r : rows) {
                    Object oid = r.get("optionId");
                    Object c = r.get("cnt");
                    if (oid != null && c != null) {
                        Long key = oid instanceof Number ? ((Number) oid).longValue() : Long.parseLong(String.valueOf(oid));
                        Integer val = c instanceof Number ? ((Number) c).intValue() : Integer.parseInt(String.valueOf(c));
                        cntMap.put(key, val);
                    }
                }
                if (s.getItems() != null) {
                    for (com.ruoyi.manage.domain.SurveyItem it : s.getItems()) {
                        if (it.getOptions() != null) {
                            for (com.ruoyi.manage.domain.SurveyOption op : it.getOptions()) {
                                Integer v = cntMap.get(op.getId());
                                if (v != null) op.setVoteCount(v);
                            }
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return success(s);
    }

    // 本类已有 answerMapper 字段为 SurveyAnswerMapper，这里通过注入的 AnswerItemMapper 获取统计能力
    private com.ruoyi.manage.mapper.SurveyAnswerItemMapper answerMapper2() {
        return this.answerItemMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/submit")
    public AjaxResult submit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List<Map<String, Object>> answers = null;
        if (body != null) {
            Object a = body.get("answers");
            if (a instanceof List) {
                answers = (List<Map<String, Object>>) a;
            }
        }
        try {
            return toAjax(service.submit(id, getUserId(), answers));
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 我的填写：返回包含 surveyId/title/submitTime 的简表
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public TableDataInfo my() {
        startPage();
        List<SurveyAnswer> list = answerMapper.selectMyAnswers(getUserId());
        // 映射 title
        Map<Long, String> titleMap = new HashMap<>();
        for (SurveyAnswer a : list) {
            if (!titleMap.containsKey(a.getSurveyId())) {
                Survey s = surveyMapper.selectById(a.getSurveyId());
                titleMap.put(a.getSurveyId(), s == null ? "-" : s.getTitle());
            }
        }
        // 输出数组
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SurveyAnswer a : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("surveyId", a.getSurveyId());
            m.put("title", titleMap.get(a.getSurveyId()));
            m.put("submitTime", a.getSubmitTime());
            rows.add(m);
        }
        return getDataTable(rows);
    }
}
