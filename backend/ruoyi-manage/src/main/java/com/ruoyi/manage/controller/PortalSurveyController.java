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
        if (s == null || s.getStatus() == null || s.getStatus() != 1) return error("问卷不存在或未发布");
        Map<Long, Object> my = service.loadMyAnswers(id, getUserId());
        s.setMyAnswers(my);
        return success(s);
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
