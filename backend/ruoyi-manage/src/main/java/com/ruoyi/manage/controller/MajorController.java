package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.service.IMajorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/major")
public class MajorController extends BaseController {

    @Autowired
    private IMajorService majorService;

    /**
     * 专业列表
     * 路径：GET /manage/major/list
     * 权限：manage:major:list
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:major:list')")
    @GetMapping("/list")
    public TableDataInfo list(Major query) {
        startPage();
        List<Major> list = majorService.selectMajorList(query);
        return getDataTable(list);
    }

    /**
     * 专业详情
     * 路径：GET /manage/major/{id}
     * 权限：manage:major:query
     *
     * @param id 专业ID
     * @return 详情
     */
    @PreAuthorize("@ss.hasPermi('manage:major:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(majorService.selectMajorById(id));
    }

    /**
     * 新增专业
     * 路径：POST /manage/major
     * 权限：manage:major:add
     *
     * @param major 实体
     * @return 操作结果
     */
    @Log(title = "专业", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:major:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Major major) {
        major.setCreateBy(getUsername());
        return toAjax(majorService.insertMajor(major));
    }

    /**
     * 编辑专业
     * 路径：PUT /manage/major
     * 权限：manage:major:edit
     *
     * @param major 实体
     * @return 操作结果
     */
    @Log(title = "专业", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:major:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Major major) {
        major.setUpdateBy(getUsername());
        return toAjax(majorService.updateMajor(major));
    }

    /**
     * 删除专业
     * 路径：DELETE /manage/major/{ids}
     * 权限：manage:major:remove
     *
     * @param ids ID数组
     * @return 操作结果
     */
    @Log(title = "专业", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:major:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(majorService.deleteMajorByIds(ids));
    }
}
