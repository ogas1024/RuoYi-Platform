package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.service.IMajorLeadService;
import com.ruoyi.manage.vo.RoleUserLeadVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/majorLead")
public class MajorLeadController extends BaseController {

    @Autowired
    private IMajorLeadService service;

    @PreAuthorize("@ss.hasPermi('manage:majorLead:list')")
    @GetMapping("/list")
    public TableDataInfo list(MajorLead query) {
        startPage();
        List<MajorLead> list = service.selectList(query);
        return getDataTable(list);
    }

    /**
     * 所有拥有 major_lead 角色的用户（可按专业筛选是否已绑定该专业）
     */
    @PreAuthorize("@ss.hasPermi('manage:majorLead:list')")
    @GetMapping("/roleUsers")
    public TableDataInfo roleUsers(@RequestParam(required = false) Long majorId) {
        startPage();
        List<RoleUserLeadVO> list = service.listRoleUsers(majorId);
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

    /**
     * 卸任负责人：删除用户的所有映射并撤销角色
     */
    @Log(title = "专业负责人-卸任", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:majorLead:remove')")
    @DeleteMapping("/retire/{userId}")
    public AjaxResult retire(@PathVariable Long userId) {
        return toAjax(service.retireUser(userId));
    }

    /**
     * 获取当前用户被分配的专业列表（仅返回 id/majorName），供课程管理页下拉使用
     */
    @PreAuthorize("@ss.hasPermi('manage:course:list')")
    @GetMapping("/myMajors")
    public AjaxResult myMajors() {
        Long uid = getUserId();
        return AjaxResult.success(service.listMyMajors(uid));
    }
}
