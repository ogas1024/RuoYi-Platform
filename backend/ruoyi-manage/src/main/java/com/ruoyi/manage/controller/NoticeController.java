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
import java.util.Collections;

@RestController
@RequestMapping("/manage/notice")
public class NoticeController extends BaseController {

    @Resource
    private INoticeService noticeService;

    @PreAuthorize("@ss.hasPermi('manage:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(Notice query) {
        startPage();
        List<Notice> list = noticeService.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Map<String, Object> data = noticeService.getDetailAndRecordRead(id);
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Notice notice) {
        int rows = noticeService.add(notice);
        // 兼容 JDK8：不能使用 Map.of，改为 Collections.singletonMap
        return rows > 0 ? AjaxResult.success(Collections.singletonMap("id", notice.getId())) : AjaxResult.error("新增失败");
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Notice notice) {
        int rows = noticeService.edit(notice);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("更新失败");
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        int rows = noticeService.removeByIds(ids);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:publish')")
    @PutMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable Long id) {
        int rows = noticeService.publish(id);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("发布失败");
    }

    @PreAuthorize("@ss.hasPermi('manage:notice:publish')")
    @PutMapping("/{id}/retract")
    public AjaxResult retract(@PathVariable Long id) {
        int rows = noticeService.retract(id);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("撤回失败");
    }

    public static class PinBody { public Boolean pinned; }

    @PreAuthorize("@ss.hasPermi('manage:notice:pin')")
    @PutMapping("/{id}/pin")
    public AjaxResult pin(@PathVariable Long id, @RequestBody PinBody body) {
        boolean pinned = body != null && Boolean.TRUE.equals(body.pinned);
        int rows = noticeService.pin(id, pinned);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("置顶失败");
    }
}
