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
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/portal/survey")
public class PortalSurveyController extends BaseController {

    @Autowired
    private ISurveyService service;

    /**
     * 门户问卷列表
     * 说明：只展示“已发布”，包含未过期与已过期。
     * @param query 查询条件
     * @return 分页数据
     */
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

    /**
     * 门户问卷详情
     * 说明：仅允许“已发布/已归档”查看；若结束则附带投票统计。
     * @param id 问卷ID
     * @return 问卷详情
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        try {
            Survey s = service.portalDetail(id, getUserId());
            return success(s);
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    

    /**
     * 提交问卷
     * @param id 问卷ID
     * @param body 请求体，包含 answers 数组
     * @return 操作结果
     */
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
    /**
     * 我的问卷填写记录
     * @return 简表（surveyId/title/submitTime）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public TableDataInfo my() {
        startPage();
        List<Map<String, Object>> rows = service.selectMyAnswerSummaries(getUserId());
        return getDataTable(rows);
    }
}
