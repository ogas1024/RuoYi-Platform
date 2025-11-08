package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.service.ICourseResourceService;
import com.ruoyi.manage.service.IScoreService;
import com.ruoyi.manage.domain.CrUserScore;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/courseResource")
public class CourseResourceController extends BaseController {

    @Autowired
    private ICourseResourceService service;
    @Autowired
    private IScoreService scoreService;

    /**
     * 列表查询
     * 路径：GET /manage/courseResource/list
     * 权限：manage:courseResource:list
     * 说明：管理端可按条件分页查询所有课程资源（不强制仅返回已通过）。
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseResource query) {
        startPage();
        List<CourseResource> list = service.selectList(query, false);
        return getDataTable(list);
    }

    /**
     * 详情
     * 路径：GET /manage/courseResource/{id}
     * 权限：manage:courseResource:query
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    /**
     * 新增
     * 路径：POST /manage/courseResource
     * 权限：manage:courseResource:add
     * 说明：创建即进入待审；自动补齐上传者与审计字段。
     */
    @Log(title = "课程资源", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CourseResource data) {
        data.setUploaderId(SecurityUtils.getUserId());
        data.setUploaderName(getUsername());
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    /**
     * 编辑
     * 路径：PUT /manage/courseResource
     * 权限：manage:courseResource:edit
     * 说明：非管理员/专业负责人仅能编辑本人资源；已通过需先下架（由 Service 校验）。
     */
    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CourseResource data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    /**
     * 批量删除
     * 路径：DELETE /manage/courseResource/{ids}
     * 权限：manage:courseResource:remove
     * 说明：管理员或专业负责人可删除任意；普通用户仅可删除本人且需满足状态限制（由 Service 校验）。
     */
    @Log(title = "课程资源", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        boolean isAdminOrLead = SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("major_lead");
        return toAjax(service.deleteByIds(ids, SecurityUtils.getUserId(), isAdminOrLead));
    }

    /**
     * 审核通过
     * 路径：PUT /manage/courseResource/{id}/approve
     * 权限：manage:courseResource:approve
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:approve')")
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        return toAjax(service.approve(id, getUsername()));
    }

    /**
     * 审核驳回
     * 路径：PUT /manage/courseResource/{id}/reject
     * 权限：manage:courseResource:reject
     * 说明：需要提供驳回理由（后端兜底为空时按空串处理）。
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:reject')")
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.reject(id, getUsername(), reason));
    }

    /**
     * 下架资源
     * 路径：PUT /manage/courseResource/{id}/offline
     * 权限：manage:courseResource:offline
     * 说明：允许管理员/负责人/上传者执行；填写原因便于审计（可空）。
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:offline')")
    @PutMapping("/{id}/offline")
    public AjaxResult offline(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.offline(id, getUsername(), reason));
    }

    /**
     * 提交上架（转待审）
     * 路径：PUT /manage/courseResource/{id}/online
     * 权限：manage:courseResource:online
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:online')")
    @PutMapping("/{id}/online")
    public AjaxResult online(@PathVariable Long id) {
        return toAjax(service.onlineToPending(id));
    }

    /**
     * 标记“最佳”
     * 路径：PUT /manage/courseResource/{id}/best
     * 权限：manage:courseResource:best
     * 说明：通常仅专业负责人可操作（由 Service 校验）。
     */
    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:best')")
    @PutMapping("/{id}/best")
    public AjaxResult best(@PathVariable Long id) {
        return toAjax(service.setBest(id, getUsername()));
    }

    /**
     * 取消“最佳”
     * 路径：PUT /manage/courseResource/{id}/unbest
     * 权限：manage:courseResource:best
     */
    @Log(title = "课程资源", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:courseResource:best')")
    @PutMapping("/{id}/unbest")
    public AjaxResult unbest(@PathVariable Long id) {
        return toAjax(service.unsetBest(id));
    }

    /**
     * 下载（302 跳转）
     * 路径：GET /manage/courseResource/{id}/download
     * 权限：manage:courseResource:download
     * 说明：
     *  - 管理端允许管理员/负责人在任意状态下载用于审核；普通用户仅允许下载已上架资源。
     *  - 若资源为外链，跳转至 linkUrl；否则跳转至文件直链 fileUrl。
     */
    @PreAuthorize("@ss.hasPermi('manage:courseResource:download')")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        CourseResource r = service.selectById(id);
        // 存在性校验
        if (r == null) {
            response.sendError(404, "资源不存在");
            return;
        }
        boolean isAdminOrLead = SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("major_lead");
        // 非管理岗仅能下载已上架
        if (!isAdminOrLead && (r.getStatus() == null || r.getStatus() != 1)) {
            response.sendError(404, "资源未上架");
            return;
        }
        // 负责人需具备该专业权限
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            Long uid = SecurityUtils.getUserId();
            boolean ok = service.userHasMajorLeadAccess(uid, r.getMajorId());
            if (!ok) {
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

    /**
     * TOP 榜（资源）
     * 路径：GET /manage/courseResource/top
     * 权限：manage:courseResource:list
     * 参数：scope=global/major/course；可选窗口 days 与 limit。
     */
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

    /**
     * 统计：上传趋势（按日计数，近 N 天）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/uploadTrend")
    public AjaxResult uploadTrend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        return success(service.uploadTrend(days));
    }

    /**
     * 统计：下载趋势（按日计数，近 N 天）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/downloadTrend")
    public AjaxResult downloadTrend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        return success(service.downloadTrend(days));
    }

    /**
     * 统计：课程资源下载榜（默认Top5，可选 days 窗口）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topResources")
    public AjaxResult topResources(@RequestParam(required = false, defaultValue = "5") Integer limit,
                                   @RequestParam(required = false, defaultValue = "30") Integer days) {
        java.util.List<com.ruoyi.manage.domain.CourseResource> list = service.selectTop("global", null, null, days, limit);
        return success(list);
    }

    /**
     * 统计：课程资源用户下载榜（默认Top5）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topDownloadUsers")
    public AjaxResult topDownloadUsers(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return success(service.topDownloadUsers(limit));
    }

    /**
     * 统计：用户积分榜（默认Top5，取全站 majorId=0）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topScoreUsers")
    public AjaxResult topScoreUsers(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        java.util.List<CrUserScore> list = scoreService.selectTopScoreUsers(limit);
        return success(list);
    }

    /**
     * 统计：专业占比（全站，全时段）
     * 路径：GET /manage/courseResource/stats/majorShare
     * 权限：已登录
     * 说明：不设时间窗口，返回累计占比。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/majorShare")
    public AjaxResult majorShare() {
        return success(service.majorShareAll());
    }

    /**
     * 统计：课程占比（全站，全时段）
     * 路径：GET /manage/courseResource/stats/courseShare
     * 权限：已登录
     * 说明：不设时间窗口，返回累计占比。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/courseShare")
    public AjaxResult courseShare() {
        return success(service.courseShareAll());
    }
}
