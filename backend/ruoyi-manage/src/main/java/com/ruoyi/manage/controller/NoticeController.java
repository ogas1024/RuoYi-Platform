package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Notice;
import com.ruoyi.manage.service.INoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/manage/notice")
public class NoticeController extends BaseController {

    @Autowired
    private INoticeService noticeService;

    /**
     * 公告列表
     * 说明：支持分页、按标题/状态筛选
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(Notice query) {
        startPage();
        List<Notice> list = noticeService.list(query);
        return getDataTable(list);
    }

    /**
     * 公告详情
     * @param id 公告ID
     * @return 公告内容及阅读记录
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Map<String, Object> data = noticeService.getDetailAndRecordRead(id);
        return AjaxResult.success(data);
    }

    /**
     * 新增公告
     * @param notice 公告实体
     * @return 新增后ID
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Notice notice) {
        int rows = noticeService.add(notice);
        // JDK8 兼容：使用 Collections.singletonMap
        return rows > 0 ? AjaxResult.success(Collections.singletonMap("id", notice.getId())) : AjaxResult.error("新增失败");
    }

    /**
     * 修改公告
     * @param notice 公告实体（含ID）
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Notice notice) {
        int rows = noticeService.edit(notice);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("更新失败");
    }

    /**
     * 批量删除
     * @param ids ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        int rows = noticeService.removeByIds(ids);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @return 操作结果（含冲突提示）
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:publish')")
    @PutMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable Long id) {
        int rows = noticeService.publish(id);
        if (rows > 0) return AjaxResult.success();
        if (rows == -404) return AjaxResult.error(404, "公告不存在或已删除");
        if (rows == -409) return AjaxResult.error(409, "状态冲突：仅草稿/撤回可发布或已发布幂等");
        return AjaxResult.error("发布失败");
    }

    /**
     * 撤回公告
     * @param id 公告ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:publish')")
    @PutMapping("/{id}/retract")
    public AjaxResult retract(@PathVariable Long id) {
        int rows = noticeService.retract(id);
        if (rows > 0) return AjaxResult.success();
        if (rows == -404) return AjaxResult.error(404, "公告不存在或已删除");
        if (rows == -409) return AjaxResult.error(409, "状态冲突：仅已发布公告可撤回");
        return AjaxResult.error("撤回失败");
    }

    public static class PinBody {
        public Boolean pinned;
    }

    /**
     * 置顶/取消置顶
     * @param id 公告ID
     * @param body { pinned: true/false }
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:notice:pin')")
    @PutMapping("/{id}/pin")
    public AjaxResult pin(@PathVariable Long id, @RequestBody PinBody body) {
        boolean pinned = body != null && Boolean.TRUE.equals(body.pinned);
        int rows = noticeService.pin(id, pinned);
        if (rows > 0) return AjaxResult.success();
        if (rows == -404) return AjaxResult.error(404, "公告不存在或已删除");
        if (rows == -409) return AjaxResult.error(409, "状态冲突：仅已发布公告可置顶");
        return AjaxResult.error("置顶失败");
    }
}
