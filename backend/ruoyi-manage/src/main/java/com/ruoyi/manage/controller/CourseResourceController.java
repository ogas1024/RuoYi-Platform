package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.service.ICourseResourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/courseResource")
public class CourseResourceController extends BaseController {

    @Resource
    private ICourseResourceService service;
    @Resource
    private com.ruoyi.manage.mapper.MajorLeadMapper majorLeadMapper;

    @PreAuthorize("@ss.hasPermi('manage:courseResource:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseResource query) {
        startPage();
        List<CourseResource> list = service.selectList(query, false);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    @Log(title = "课程资源", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CourseResource data) {
        data.setUploaderId(SecurityUtils.getUserId());
        data.setUploaderName(getUsername());
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CourseResource data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @Log(title = "课程资源", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        boolean isAdminOrLead = SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("major_lead");
        return toAjax(service.deleteByIds(ids, SecurityUtils.getUserId(), isAdminOrLead));
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:approve')")
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        return toAjax(service.approve(id, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:reject')")
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.reject(id, getUsername(), reason));
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:offline')")
    @PutMapping("/{id}/offline")
    public AjaxResult offline(@PathVariable Long id) {
        return toAjax(service.offline(id));
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:online')")
    @PutMapping("/{id}/online")
    public AjaxResult online(@PathVariable Long id) {
        return toAjax(service.onlineToPending(id));
    }

    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:best')")
    @PutMapping("/{id}/best")
    public AjaxResult best(@PathVariable Long id) {
        return toAjax(service.setBest(id, getUsername()));
    }

    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:best')")
    @PutMapping("/{id}/unbest")
    public AjaxResult unbest(@PathVariable Long id) {
        return toAjax(service.unsetBest(id));
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:download')")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        CourseResource r = service.selectById(id);
        if (r == null) {
            response.sendError(404, "资源不存在");
            return;
        }
        boolean isAdminOrLead = SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("major_lead");
        if (!isAdminOrLead && (r.getStatus() == null || r.getStatus() != 1)) {
            response.sendError(404, "资源未上架");
            return;
        }
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            Long uid = SecurityUtils.getUserId();
            Integer ok = majorLeadMapper.existsUserMajor(uid, r.getMajorId());
            if (ok == null || ok == 0) {
                response.sendError(403, "无权下载该专业资源");
                return;
            }
        }
        // 管理端下载：允许 admin/major_lead 在任意状态下下载进行审核
        service.incrDownload(id);
        String target = r.getResourceType() != null && r.getResourceType() == 1 ? r.getLinkUrl() : r.getFileUrl();
        response.setStatus(HttpServletResponse.SC_FOUND); // 302
        response.setHeader("Location", target);
    }

    @PreAuthorize("@ss.hasPermi('manage:courseResource:list')")
    @GetMapping("/top")
    public AjaxResult top(@RequestParam(defaultValue = "global") String scope,
                          @RequestParam(required = false) Long majorId,
                          @RequestParam(required = false) Long courseId,
                          @RequestParam(required = false, defaultValue = "7") Integer days,
                          @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<CourseResource> list = service.selectTop(scope, majorId, courseId, days, limit);
        return success(list);
    }
}
