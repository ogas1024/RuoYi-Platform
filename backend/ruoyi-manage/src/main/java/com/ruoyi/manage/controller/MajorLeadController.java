package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.service.IMajorLeadService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/manage/majorLead")
public class MajorLeadController extends BaseController {

    @Resource
    private IMajorLeadService service;

    @PreAuthorize("@ss.hasPermi('manage:majorLead:list')")
    @GetMapping("/list")
    public TableDataInfo list(MajorLead query) {
        startPage();
        List<MajorLead> list = service.selectList(query);
        return getDataTable(list);
    }

    @Log(title = "专业负责人", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:majorLead:add')")
    @PostMapping
    public AjaxResult add(@RequestBody MajorLead data) {
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "专业负责人", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:majorLead:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }

    @Log(title = "专业负责人", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:majorLead:remove')")
    @DeleteMapping
    public AjaxResult removeByMajorAndUser(@RequestParam Long majorId, @RequestParam Long userId) {
        return toAjax(service.deleteByMajorAndUser(majorId, userId));
    }
}

