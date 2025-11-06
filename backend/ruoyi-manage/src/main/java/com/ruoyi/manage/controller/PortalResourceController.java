package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.service.ICourseResourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/portal/resource")
public class PortalResourceController extends BaseController {

    @Resource
    private ICourseResourceService service;

    // 仅返回已通过资源
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(CourseResource query) {
        startPage();
        List<CourseResource> list = service.selectList(query, true);
        return getDataTable(list);
    }

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    public AjaxResult top(@RequestParam(defaultValue = "global") String scope,
                          @RequestParam(required = false) Long majorId,
                          @RequestParam(required = false) Long courseId,
                          @RequestParam(required = false, defaultValue = "7") Integer days,
                          @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return success(service.selectTop(scope, majorId, courseId, days, limit));
    }
}
