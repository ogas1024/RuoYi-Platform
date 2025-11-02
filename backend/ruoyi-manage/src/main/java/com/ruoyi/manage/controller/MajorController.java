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

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/manage/major")
public class MajorController extends BaseController {

    @Resource
    private IMajorService majorService;

    @PreAuthorize("@ss.hasPermi('manage:major:list')")
    @GetMapping("/list")
    public TableDataInfo list(Major query) {
        startPage();
        List<Major> list = majorService.selectMajorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:major:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(majorService.selectMajorById(id));
    }

    @Log(title = "专业", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:major:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Major major) {
        major.setCreateBy(getUsername());
        return toAjax(majorService.insertMajor(major));
    }

    @Log(title = "专业", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:major:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Major major) {
        major.setUpdateBy(getUsername());
        return toAjax(majorService.updateMajor(major));
    }

    @Log(title = "专业", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:major:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(majorService.deleteMajorByIds(ids));
    }
}

