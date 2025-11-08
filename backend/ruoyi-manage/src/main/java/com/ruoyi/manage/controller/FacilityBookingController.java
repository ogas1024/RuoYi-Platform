package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.service.IFacilityBookingService;
import com.ruoyi.manage.vo.FacilityBookingCreateRequest;
import com.ruoyi.manage.vo.FacilityBookingEndEarlyRequest;
import com.ruoyi.manage.vo.FacilityBookingUpdateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/booking")
public class FacilityBookingController extends BaseController {

    @Autowired
    private IFacilityBookingService service;

    /**
     * 新增预约
     * 说明：校验房间状态/封禁状态/时间窗口/冲突/使用人列表等。
     * @param body 创建请求体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:add')")
    @Log(title = "功能房-预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBookingCreateRequest body) {
        FacilityBooking data = new FacilityBooking();
        data.setRoomId(body.getRoomId());
        data.setPurpose(body.getPurpose());
        data.setStartTime(body.getStartTime());
        data.setEndTime(body.getEndTime());
        // 创建校验与落库由 service 统一处理
        int n = service.create(data, body.getUserIdList(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    /**
     * 我的预约列表
     * @param status 状态筛选（可选）
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:list')")
    @GetMapping("/my/list")
    public TableDataInfo myList(@RequestParam(required = false) String status) {
        startPage();
        List<FacilityBooking> list = service.myList(SecurityUtils.getUserId(), status);
        return getDataTable(list);
    }

    /**
     * 修改预约（开场前）
     * 说明：仅允许待审核/已批准状态，且需通过冲突及时间窗口校验。
     * @param id 预约ID
     * @param body 更新请求
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:edit')")
    @Log(title = "功能房-预约", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody FacilityBookingUpdateRequest body) {
        FacilityBooking patch = new FacilityBooking();
        patch.setPurpose(body.getPurpose());
        patch.setStartTime(body.getStartTime());
        patch.setEndTime(body.getEndTime());
        // 仅在开始前允许修改；幂等/冲突校验在 service
        int n = service.updateBeforeStart(id, patch, body.getUserIdList(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    /**
     * 提前结束
     * 说明：仅已批准/进行中允许；结束时间将不早于当前时刻。
     * @param id 预约ID
     * @param body 结束时间
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:edit')")
    @Log(title = "功能房-预约提前结束", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/end-early")
    public AjaxResult endEarly(@PathVariable Long id, @RequestBody FacilityBookingEndEarlyRequest body) {
        int n = service.endEarly(id, body.getEndTime(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    /**
     * 取消预约
     * 说明：仅申请人可取消，且需未开始。
     * @param id 预约ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:cancel')")
    @Log(title = "功能房-预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult cancel(@PathVariable Long id) {
        return toAjax(service.cancel(id, SecurityUtils.getUserId()));
    }

    // 审核
    /**
     * 审核列表
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:list')")
    @GetMapping("/audit/list")
    public TableDataInfo auditList(@RequestParam(required = false) Long bookingId,
                                   @RequestParam(required = false) Long buildingId,
                                   @RequestParam(required = false) Long roomId,
                                   @RequestParam(required = false) Long applicantId,
                                   @RequestParam(required = false, name = "applicantUserName") String applicantUserName,
                                   @RequestParam(required = false) java.util.Date from,
                                   @RequestParam(required = false) java.util.Date to) {
        startPage();
        // 支持用户名筛选：解析为 applicantId
        if ((applicantId == null || applicantId == 0) && applicantUserName != null && !applicantUserName.trim().isEmpty()) {
            Long uid = com.ruoyi.common.utils.spring.SpringUtils.getBean(com.ruoyi.manage.mapper.SysLinkageMapper.class)
                    .selectUserIdByUserName(applicantUserName.trim());
            if (uid != null) applicantId = uid;
        }
        java.util.List<com.ruoyi.manage.domain.FacilityBooking> list = service.auditList(bookingId, buildingId, roomId, applicantId, from, to);
        return getDataTable(list);
    }

    /**
     * 审核通过
     * @param id 预约ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:approve')")
    @Log(title = "功能房-预约审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        return toAjax(service.approve(id, getUsername()));
    }

    /**
     * 审核驳回
     * @param id 预约ID
     * @param body { reason: 驳回理由 }
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:reject')")
    @Log(title = "功能房-预约审核驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.reject(id, getUsername(), reason));
    }

    /**
     * 预约详情
     * 说明：组装房间/楼房/申请人元信息与状态文字。
     * @param id 预约ID
     * @return 详情 + 用户列表 + 元信息
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.getDetailWithMeta(id));
    }
}
