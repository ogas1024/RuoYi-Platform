package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FacilityBuilding;
import com.ruoyi.manage.service.IFacilityBuildingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/building")
public class FacilityBuildingController extends BaseController {

    @Resource
    private IFacilityBuildingService service;

    @PreAuthorize("@ss.hasPermi('manage:facility:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(FacilityBuilding query) {
        startPage();
        List<FacilityBuilding> list = service.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:building:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:add')")
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBuilding data) {
        if (data.getStatus() == null || data.getStatus().trim().isEmpty()) {
            data.setStatus("0");
        }
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody FacilityBuilding data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }
}
