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

    /**
     * 封禁记录列表
     * 路径：GET /manage/facility/ban/list
     * 权限：manage:facility:ban:list
     * 说明：支持按状态/用户ID/用户名筛选，分页返回。
     *
     * @param status  状态（0启用 1禁用）
     * @param userId  用户ID（可选）
     * @param username 用户名（可选，服务层解析为 userId）
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:ban:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String status,
                              @RequestParam(required = false) Long userId,
                              @RequestParam(required = false, name = "username") String username) {
        startPage();
        List<FacilityBan> list = service.list(status, userId, username);
        return getDataTable(list);
    }

    /**
     * 新增封禁
     * 路径：POST /manage/facility/ban
     * 权限：manage:facility:ban:add
     * 说明：支持以用户名指定用户；默认 status=0 生效。
     *
     * @param data 封禁实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:ban:add')")
    @Log(title = "功能房-封禁", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBan data) {
        data.setCreateBy(getUsername());
        data.setStatus(data.getStatus() == null ? "0" : data.getStatus());
        // 支持用用户名提交
        if ((data.getUserId() == null || data.getUserId() == 0) && data.getUsername() != null && !data.getUsername().trim().isEmpty()) {
            Long uid = com.ruoyi.common.utils.spring.SpringUtils.getBean(com.ruoyi.manage.mapper.SysLinkageMapper.class)
                    .selectUserIdByUserName(data.getUsername().trim());
            if (uid != null) data.setUserId(uid);
        }
        return toAjax(service.ban(data));
    }

    /**
     * 解除封禁
     * 路径：DELETE /manage/facility/ban/{id}
     * 权限：manage:facility:ban:remove
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:ban:remove')")
    @Log(title = "功能房-封禁", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(service.unban(id));
    }
}
