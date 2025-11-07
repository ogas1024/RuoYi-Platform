package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Notice;
import com.ruoyi.manage.service.INoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 门户通知公告接口：仅需登录即可访问。
 * 与管理端 /manage/notice 区分，避免按钮级权限拦截。
 */
@RestController
@RequestMapping("/portal/notice")
public class PortalNoticeController extends BaseController {

    @Resource
    private INoticeService noticeService;

    /**
     * 门户列表：仅返回有权可见的记录（服务层已做范围过滤）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(Notice query) {
        startPage();
        // 门户默认只看“已发布”；如需支持“已读/未读/排序”等，直接透传 query。
        List<Notice> list = noticeService.list(query);
        return getDataTable(list);
    }

    /**
     * 门户详情：记录阅读回执（幂等），返回附件与范围信息。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Map<String, Object> data = noticeService.getDetailAndRecordRead(id);
        return AjaxResult.success(data);
    }
}

