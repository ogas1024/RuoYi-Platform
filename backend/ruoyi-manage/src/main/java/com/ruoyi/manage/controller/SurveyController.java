package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Survey;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.ISurveyService;
import com.ruoyi.manage.mapper.SurveyAnswerMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/survey")
public class SurveyController extends BaseController {

    @Resource
    private ISurveyService service;
    @Resource
    private SurveyAnswerMapper answerMapper;
    @Resource
    private com.ruoyi.manage.mapper.SurveyAnswerItemMapper answerItemMapper;

    @PreAuthorize("@ss.hasPermi('manage:survey:list')")
    @GetMapping("/list")
    public TableDataInfo list(Survey query) {
        startPage();
        List<Survey> list = service.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Survey s = service.detail(id, true);
        if (s == null) return error("问卷不存在");
        // 管理详情：补充每个选项的票数统计（仅选择题）。
        try {
            java.util.List<java.util.Map<String, Object>> rows = answerItemMapper.countOptionVotes(id);
            java.util.Map<Long, Integer> cntMap = new java.util.HashMap<>();
            for (java.util.Map<String, Object> r : rows) {
                if (r == null) continue;
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
        } catch (Exception ignore) {}
        return success(s);
    }

    /** 提交用户列表：用于管理侧详情页展示已提交用户 */
    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}/submits")
    public AjaxResult submitUsers(@PathVariable Long id) {
        java.util.List<java.util.Map<String, Object>> rows = answerMapper.selectSubmitUsers(id);
        return success(rows);
    }

    /** 指定用户的答卷详情：返回包含 myAnswers 的 Survey */
    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}/answers/{userId}")
    public AjaxResult userAnswers(@PathVariable Long id, @PathVariable Long userId) {
        Survey s = service.detail(id, false);
        if (s == null) return error("问卷不存在");
        java.util.Map<Long, Object> my = service.loadMyAnswers(id, userId);
        s.setMyAnswers(my);
        return success(s);
    }

    @Log(title = "问卷", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:survey:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Survey data) {
        int rows = service.add(data);
        if (rows > 0) {
            java.util.Map<String, Object> res = new java.util.HashMap<>();
            res.put("id", data.getId());
            return success(res);
        }
        return error("保存失败");
    }

    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Survey data) {
        try {
            int n = service.edit(data);
            if (n == -404) return error("问卷不存在");
            return toAjax(n);
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:archive')")
    @PutMapping("/{id}/archive")
    public AjaxResult archive(@PathVariable Long id) {
        int n = service.archive(id);
        if (n == -404) return error("问卷不存在");
        return toAjax(n);
    }

    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:extend')")
    @PutMapping("/{id}/extend")
    public AjaxResult extend(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String deadlineStr = body == null ? null : body.get("deadline");
        try {
            Date newDeadline = deadlineStr == null ? null : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadlineStr);
            return toAjax(service.extend(id, newDeadline));
        } catch (ServiceException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:pin')")
    @PutMapping("/{id}/pin")
    public AjaxResult pin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean pinned = false;
        if (body != null && body.get("pinned") != null) {
            Object v = body.get("pinned");
            if (v instanceof Boolean) pinned = (Boolean) v; else pinned = "1".equals(String.valueOf(v)) || "true".equalsIgnoreCase(String.valueOf(v));
        }
        int n = service.pin(id, pinned);
        if (n == -404) return error("问卷不存在");
        if (n == -409) return error("仅已发布的问卷可置顶");
        return toAjax(n);
    }

    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:publish')")
    @PutMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable Long id) {
        int n = service.publish(id);
        if (n == -404) return error("问卷不存在");
        if (n == -409) return error("仅草稿可发布");
        return toAjax(n);
    }
}
