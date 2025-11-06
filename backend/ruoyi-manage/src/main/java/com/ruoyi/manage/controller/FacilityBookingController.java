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

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/booking")
public class FacilityBookingController extends BaseController {

    @Resource
    private IFacilityBookingService service;
    @Resource
    private com.ruoyi.manage.mapper.FacilityBookingMapper bookingMapper;
    @Resource
    private com.ruoyi.manage.mapper.FacilityBookingUserMapper bookingUserMapper;
    @Resource
    private com.ruoyi.manage.mapper.FacilityRoomMapper roomMapper;
    @Resource
    private com.ruoyi.manage.mapper.FacilityBuildingMapper buildingMapper;
    @Resource
    private com.ruoyi.manage.mapper.UserMapper userMapper;

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:add')")
    @Log(title = "功能房-预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBookingCreateRequest body) {
        FacilityBooking data = new FacilityBooking();
        data.setRoomId(body.getRoomId());
        data.setPurpose(body.getPurpose());
        data.setStartTime(body.getStartTime());
        data.setEndTime(body.getEndTime());
        int n = service.create(data, body.getUserIdList(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:list')")
    @GetMapping("/my/list")
    public TableDataInfo myList(@RequestParam(required = false) String status) {
        startPage();
        List<FacilityBooking> list = service.myList(SecurityUtils.getUserId(), status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:edit')")
    @Log(title = "功能房-预约", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody FacilityBookingUpdateRequest body) {
        FacilityBooking patch = new FacilityBooking();
        patch.setPurpose(body.getPurpose());
        patch.setStartTime(body.getStartTime());
        patch.setEndTime(body.getEndTime());
        int n = service.updateBeforeStart(id, patch, body.getUserIdList(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:edit')")
    @Log(title = "功能房-预约提前结束", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/end-early")
    public AjaxResult endEarly(@PathVariable Long id, @RequestBody FacilityBookingEndEarlyRequest body) {
        int n = service.endEarly(id, body.getEndTime(), SecurityUtils.getUserId(), getUsername());
        return toAjax(n);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:cancel')")
    @Log(title = "功能房-预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult cancel(@PathVariable Long id) {
        return toAjax(service.cancel(id, SecurityUtils.getUserId()));
    }

    // 审核
    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:list')")
    @GetMapping("/audit/list")
    public TableDataInfo auditList(@RequestParam(required = false) Long bookingId,
                                   @RequestParam(required = false) Long buildingId,
                                   @RequestParam(required = false) Long roomId,
                                   @RequestParam(required = false) Long applicantId,
                                   @RequestParam(required = false) java.util.Date from,
                                   @RequestParam(required = false) java.util.Date to) {
        startPage();
        java.util.List<com.ruoyi.manage.domain.FacilityBooking> list = service.auditList(bookingId, buildingId, roomId, applicantId, from, to);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:approve')")
    @Log(title = "功能房-预约审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        return toAjax(service.approve(id, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:audit:reject')")
    @Log(title = "功能房-预约审核驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.reject(id, getUsername(), reason));
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:booking:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        FacilityBooking b = bookingMapper.selectById(id);
        if (b == null) return error("记录不存在");
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("booking", b);
        res.put("users", bookingUserMapper.selectByBookingId(id));
        java.util.Map<String, Object> meta = new java.util.HashMap<>();
        // 房间/楼房名
        String roomName = null, buildingName = null;
        try {
            com.ruoyi.manage.domain.FacilityRoom room = roomMapper.selectById(b.getRoomId());
            if (room != null) {
                roomName = room.getRoomName();
                com.ruoyi.manage.domain.FacilityBuilding bd = buildingMapper.selectById(room.getBuildingId());
                if (bd != null) buildingName = bd.getBuildingName();
            }
        } catch (Exception ignored) {}
        meta.put("roomName", roomName);
        meta.put("buildingName", buildingName);
        // 申请人昵称/用户名
        String applicantName = null;
        try {
            com.ruoyi.manage.domain.User q = new com.ruoyi.manage.domain.User();
            q.setUserId(b.getApplicantId());
            java.util.List<com.ruoyi.manage.domain.User> us = userMapper.selectUserList(q);
            if (us != null && !us.isEmpty()) {
                com.ruoyi.manage.domain.User u = us.get(0);
                applicantName = (u.getNickName() != null && !u.getNickName().isEmpty()) ? u.getNickName() : u.getUserName();
            }
        } catch (Exception ignored) {}
        meta.put("applicantName", applicantName);
        // 状态文字
        String statusText;
        String st = b.getStatus();
        if ("0".equals(st)) statusText = "待审核";
        else if ("1".equals(st)) statusText = "已批准";
        else if ("2".equals(st)) statusText = "已驳回";
        else if ("3".equals(st)) statusText = "已取消";
        else if ("4".equals(st)) statusText = "进行中";
        else if ("5".equals(st)) statusText = "已完成";
        else statusText = "-";
        meta.put("statusText", statusText);
        res.put("meta", meta);
        return success(res);
    }
}
