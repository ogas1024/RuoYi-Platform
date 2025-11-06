package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FacilitySetting;
import com.ruoyi.manage.service.IFacilitySettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/manage/facility/setting")
public class FacilitySettingController extends BaseController {

    @Resource
    private IFacilitySettingService service;

    @PreAuthorize("@ss.hasPermi('manage:facility:setting:get')")
    @GetMapping("/audit-required")
    public AjaxResult get() {
        FacilitySetting s = service.get();
        return success(new java.util.HashMap<String, Object>() {{
            put("auditRequired", "1".equals(s.getAuditRequired()));
            put("maxDurationMinutes", s.getMaxDurationMinutes());
        }});
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:setting:edit')")
    @Log(title = "功能房-设置", businessType = BusinessType.UPDATE)
    @PutMapping("/audit-required")
    public AjaxResult save(@RequestBody java.util.Map<String, Object> body) {
        FacilitySetting s = new FacilitySetting();
        s.setId(1L);
        Object auditRequired = body.get("auditRequired");
        s.setAuditRequired(Boolean.TRUE.equals(auditRequired) || "true".equalsIgnoreCase(String.valueOf(auditRequired)) ? "1" : "0");
        Object max = body.get("maxDurationMinutes");
        s.setMaxDurationMinutes(max == null ? 4320 : Integer.parseInt(String.valueOf(max)));
        // 首次保存需要 insert，需补齐创建人/创建时间
        s.setCreateBy(getUsername());
        s.setCreateTime(new java.util.Date());
        s.setUpdateBy(getUsername());
        s.setUpdateTime(new java.util.Date());
        return toAjax(service.save(s));
    }
}
