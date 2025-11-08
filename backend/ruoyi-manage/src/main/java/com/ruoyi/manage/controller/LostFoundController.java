package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.LostItem;
import com.ruoyi.manage.domain.LostItemImage;
import com.ruoyi.manage.service.ILostItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/manage/lostfound")
public class LostFoundController extends BaseController {

    @Autowired
    private ILostItemService service;

    /**
     * 列表查询（已发布）
     * 说明：返回状态为“已发布(2)”的失物招领记录，支持分页参数。
     * @param query 查询条件（标题、类型、时间范围等）
     * @return 分页数据表格
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:list')")
    @GetMapping("/list")
    public TableDataInfo list(LostItem query) {
        startPage();
        List<LostItem> list = service.selectList(query, Collections.singletonList(2));
        return getDataTable(list);
    }

    /**
     * 审核列表（待审）
     * 说明：返回状态为“待审核(1)”的数据，供后台审核使用。
     * @param query 查询条件
     * @return 分页数据表格
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:audit:list')")
    @GetMapping("/audit/list")
    public TableDataInfo auditList(LostItem query) {
        startPage();
        List<LostItem> list = service.selectList(query, Collections.singletonList(1));
        return getDataTable(list);
    }

    /**
     * 回收站列表（驳回/下架）
     * 说明：返回状态“已驳回(3)”与“已下架(4)”的数据。
     * @param query 查询条件
     * @return 分页数据表格
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:recycle:list')")
    @GetMapping("/recycle/list")
    public TableDataInfo recycleList(LostItem query) {
        startPage();
        List<LostItem> list = service.selectList(query, Arrays.asList(3, 4));
        return getDataTable(list);
    }

    /**
     * 查看详情
     * @param id 主键ID
     * @return 详情及图片数组
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:get')")
    @GetMapping("/{id}")
    public AjaxResult info(@PathVariable Long id) {
        LostItem data = service.selectById(id);
        // 校验存在性
        if (data == null) return error("条目不存在");
        List<LostItemImage> images = service.listImages(id);
        Map<String, Object> res = new HashMap<>();
        res.put("item", data);
        res.put("images", images);
        return success(res);
    }

    /**
     * 新增失物招领
     * @param body 失物信息（含图片）
     * @return 成功返回新建ID
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:add')")
    @PostMapping
    public AjaxResult add(@RequestBody LostItem body) {
        body.setCreateBy(getUsername());
        int n = service.insert(body);
        if (n > 0) {
            Map<String, Object> d = new HashMap<>();
            d.put("id", body.getId());
            return success(d);
        }
        return error("保存失败");
    }

    /**
     * 修改失物招领
     * @param body 失物信息
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody LostItem body) {
        // 参数校验
        if (body == null || body.getId() == null) return error("参数不能为空");
        body.setUpdateBy(getUsername());
        return service.update(body) > 0 ? success() : error("更新失败");
    }

    public static class ReasonBody {
        public String reason;
    }

    public static class SolveBody {
        public Boolean solved;
    }

    /**
     * 审核通过
     * @param id 主键ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:audit:approve')")
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        int n = service.approve(id, getUsername());
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅待审可通过");
    }

    /**
     * 审核驳回
     * @param id 主键ID
     * @param body 驳回原因
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:audit:reject')")
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody ReasonBody body) {
        // 必填项校验
        if (body == null || body.reason == null || body.reason.trim().isEmpty()) return error("驳回原因必填");
        int n = service.reject(id, getUsername(), body.reason);
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅待審可驳回");
    }

    /**
     * 下架记录
     * @param id 主键ID
     * @param body 下架原因
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:offline')")
    @PutMapping("/{id}/offline")
    public AjaxResult offline(@PathVariable Long id, @RequestBody ReasonBody body) {
        // 必填项校验
        if (body == null || body.reason == null || body.reason.trim().isEmpty()) return error("下架原因必填");
        int n = service.offline(id, getUsername(), body.reason);
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅已发布可下架");
    }

    /**
     * 恢复为待审
     * @param id 主键ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:restore')")
    @PutMapping("/{id}/restore")
    public AjaxResult restore(@PathVariable Long id) {
        int n = service.restore(id, getUsername());
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅驳回/下架可恢复为待审");
    }

    /**
     * 管理员设置解决状态
     * @param id 主键ID
     * @param body { solved: true/false }
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:solve')")
    @PutMapping("/{id}/solve")
    public AjaxResult solveAdmin(@PathVariable Long id, @RequestBody SolveBody body) {
        boolean solved = body != null && Boolean.TRUE.equals(body.solved);
        int n = service.setSolvedAdmin(id, solved, getUsername());
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅已发布可调整解决状态");
    }

    /**
     * 批量删除（软删）
     * @param ids 主键ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:lostfound:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        int total = 0;
        for (Long id : ids) total += service.softDeleteById(id, getUsername(), true);
        return total > 0 ? success() : error("删除失败");
    }
}
