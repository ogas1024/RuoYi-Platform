package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FacilityBan;
import com.ruoyi.manage.service.IFacilityBanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/ban")
public class FacilityBanController extends BaseController {

    @Autowired
    private IFacilityBanService service;

    @PreAuthorize("@ss.hasPermi('manage:facility:ban:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String status, @RequestParam(required = false) Long userId) {
        startPage();
        List<FacilityBan> list = service.list(status, userId);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:ban:add')")
    @Log(title = "功能房-封禁", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBan data) {
        data.setCreateBy(getUsername());
        data.setStatus(data.getStatus() == null ? "0" : data.getStatus());
        return toAjax(service.ban(data));
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:ban:remove')")
    @Log(title = "功能房-封禁", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(service.unban(id));
    }
}
