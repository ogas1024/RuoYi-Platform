package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.service.ICourseResourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/portal/resource")
public class PortalResourceController extends BaseController {

    @Autowired
    private ICourseResourceService service;

    /**
     * 门户资源列表
     * 路径：GET /portal/resource/list
     * 权限：已登录（isAuthenticated）
     * 说明：仅返回已通过（已上架）资源；支持分页与条件过滤。
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(CourseResource query) {
        startPage();
        List<CourseResource> list = service.selectList(query, true);
        return getDataTable(list);
    }

    /**
     * 资源详情（门户）
     * 路径：GET /portal/resource/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：非上架仅允许上传者本人查看。
     *
     * @param id 资源ID
     * @return 资源详情
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult info(@PathVariable Long id) {
        CourseResource r = service.selectById(id);
        if (r == null) return error("资源不存在");
        if (r.getStatus() == null || r.getStatus() != 1) {
            Long uid = getUserId();
            if (uid == null || !uid.equals(r.getUploaderId())) {
                return error("资源不存在或未上架");
            }
        }
        return success(r);
    }

    /**
     * 下载/打开资源（门户，302跳转）
     * 路径：GET /portal/resource/{id}/download
     * 权限：已登录（isAuthenticated）
     * 说明：仅允许上架资源；自动选择外链或文件直链。
     *
     * @param id 资源ID
     * @param response Http响应
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        CourseResource r = service.selectById(id);
        if (r == null || r.getStatus() == null || r.getStatus() != 1) {
            response.sendError(404, "资源不存在或未上架");
            return;
        }
        service.incrDownload(id);
        String target = r.getResourceType() != null && r.getResourceType() == 1 ? r.getLinkUrl() : r.getFileUrl();
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", target);
    }

    /**
     * TOP 榜（门户）
     * 路径：GET /portal/resource/top
     * 权限：已登录（isAuthenticated）
     * 说明：scope=global/major/course，支持 limit 与天数窗口。
     *
     * @param scope   范围（global/major/course）
     * @param majorId 专业ID（可选）
     * @param courseId 课程ID（可选）
     * @param days    统计窗口天数（默认7）
     * @param limit   TopN（默认10）
     * @return 列表
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    public AjaxResult top(@RequestParam(defaultValue = "global") String scope,
                          @RequestParam(required = false) Long majorId,
                          @RequestParam(required = false) Long courseId,
                          @RequestParam(required = false, defaultValue = "7") Integer days,
                          @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return success(service.selectTop(scope, majorId, courseId, days, limit));
    }

    // ========= 门户管理：我的/新增/编辑/删除/上下架 =========

    /**
     * 我的资源列表：仅当前用户上传的资源，所有状态。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/list")
    public TableDataInfo myList(CourseResource query) {
        startPage();
        if (query == null) query = new CourseResource();
        query.setUploaderId(getUserId());
        List<CourseResource> list = service.selectList(query, false);
        return getDataTable(list);
    }

    /**
     * 新增资源（进入待审）。
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public AjaxResult add(@RequestBody CourseResource data) {
        if (data == null) return error("参数不能为空");
        data.setUploaderId(getUserId());
        data.setUploaderName(getUsername());
        data.setCreateBy(getUsername());
        int rows = service.insert(data);
        if (rows > 0) {
            java.util.Map<String, Object> res = new java.util.HashMap<>();
            res.put("id", data.getId());
            return success(res);
        }
        return error("保存失败");
    }

    /**
     * 编辑资源（仅本人；编辑后回到待审）。
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public AjaxResult edit(@RequestBody CourseResource data) {
        if (data == null || data.getId() == null) return error("参数不能为空");
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    /**
     * 删除资源（仅本人；待审/驳回/下架可删除）。
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(service.deleteByIds(new Long[]{id}, getUserId(), false));
    }

    /**
     * 提交上架（转为待审）。
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/online")
    public AjaxResult online(@PathVariable Long id) {
        return toAjax(service.onlineToPending(id));
    }

    /**
     * 下架（仅本人或有权者）。
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/offline")
    public AjaxResult offline(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.offline(id, getUsername(), reason));
    }
}
