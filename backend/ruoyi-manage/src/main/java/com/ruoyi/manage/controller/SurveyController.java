package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Survey;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.service.ISurveyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/survey")
public class SurveyController extends BaseController {

    @Autowired
    private ISurveyService service;

    /**
     * 列表查询
     * 路径：GET /manage/survey/list
     * 权限：manage:survey:list
     * 说明：支持按标题/状态/时间等条件查询并分页。
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:survey:list')")
    @GetMapping("/list")
    public TableDataInfo list(Survey query) {
        startPage();
        List<Survey> list = service.list(query);
        return getDataTable(list);
    }

    /**
     * 详情（含基础统计）
     * 路径：GET /manage/survey/{id}
     * 权限：manage:survey:query
     * 说明：返回问卷详情与统计数据（选项计数等）。
     *
     * @param id 问卷ID
     * @return 问卷详情
     */
    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        try {
            Survey s = service.manageDetailWithStats(id);
            return success(s);
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 提交用户列表：用于管理侧详情页展示已提交用户
     */
    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}/submits")
    public AjaxResult submitUsers(@PathVariable Long id) {
        return success(service.selectSubmitUsers(id));
    }

    /**
     * 指定用户的答卷详情：返回包含 myAnswers 的 Survey
     */
    @PreAuthorize("@ss.hasPermi('manage:survey:query')")
    @GetMapping("/{id}/answers/{userId}")
    public AjaxResult userAnswers(@PathVariable Long id, @PathVariable Long userId) {
        try {
            return success(service.surveyWithUserAnswers(id, userId));
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 新增问卷
     * 路径：POST /manage/survey
     * 权限：manage:survey:add
     * 说明：支持一次性提交题目与选项，后续可编辑。
     *
     * @param data 问卷实体
     * @return 新建ID
     */
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

    /**
     * 编辑问卷
     * 路径：PUT /manage/survey
     * 权限：manage:survey:edit
     * 说明：未发布可编辑基础信息与题目；发布后允许有限字段修改（由服务层校验）。
     *
     * @param data 问卷实体
     * @return 操作结果
     */
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

    /**
     * 归档问卷
     * 路径：PUT /manage/survey/{id}/archive
     * 权限：manage:survey:archive
     * 说明：归档后不可再提交。
     *
     * @param id 问卷ID
     * @return 操作结果
     */
    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:archive')")
    @PutMapping("/{id}/archive")
    public AjaxResult archive(@PathVariable Long id) {
        int n = service.archive(id);
        if (n == -404) return error("问卷不存在");
        return toAjax(n);
    }

    /**
     * 延长截止时间
     * 路径：PUT /manage/survey/{id}/extend
     * 权限：manage:survey:extend
     * 说明：时间格式 yyyy-MM-dd HH:mm:ss。
     *
     * @param id   问卷ID
     * @param body { deadline: string }
     * @return 操作结果/错误提示
     */
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

    /**
     * 置顶/取消置顶
     * 路径：PUT /manage/survey/{id}/pin
     * 权限：manage:survey:pin
     * 说明：仅“已发布”的问卷可置顶。
     *
     * @param id   问卷ID
     * @param body { pinned: true/false }
     * @return 操作结果/冲突码
     */
    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:pin')")
    @PutMapping("/{id}/pin")
    public AjaxResult pin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean pinned = false;
        if (body != null && body.get("pinned") != null) {
            Object v = body.get("pinned");
            if (v instanceof Boolean) pinned = (Boolean) v;
            else pinned = "1".equals(String.valueOf(v)) || "true".equalsIgnoreCase(String.valueOf(v));
        }
        int n = service.pin(id, pinned);
        if (n == -404) return error("问卷不存在");
        if (n == -409) return error("仅已发布的问卷可置顶");
        return toAjax(n);
    }

    /**
     * 发布
     * 路径：PUT /manage/survey/{id}/publish
     * 权限：manage:survey:publish
     * 说明：仅草稿可发布。
     *
     * @param id 问卷ID
     * @return 操作结果
     */
    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:survey:publish')")
    @PutMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable Long id) {
        int n = service.publish(id);
        if (n == -404) return error("问卷不存在");
        if (n == -409) return error("仅草稿可发布");
        return toAjax(n);
    }

    /**
     * 生成 AI 汇总报告
     */
    @Log(title = "问卷", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('manage:survey:summary')")
    @PostMapping("/{id}/ai-summary")
    public AjaxResult aiSummary(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String extra = body == null ? null : String.valueOf(body.getOrDefault("extraPrompt", ""));
        try {
            String text = service.aiSummaryReport(id, extra);
            java.util.Map<String, Object> res = new java.util.HashMap<>();
            res.put("text", text);
            return success(res);
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }
}
