package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.domain.FacilityBuilding;
import com.ruoyi.manage.domain.FacilityRoom;
import com.ruoyi.manage.service.*;
import com.ruoyi.manage.vo.FacilityBookingCreateRequest;
import com.ruoyi.manage.vo.FacilityBookingEndEarlyRequest;
import com.ruoyi.manage.vo.FacilityBookingUpdateRequest;
import com.ruoyi.manage.vo.TimelineSegmentVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/portal/facility")
public class PortalFacilityController extends BaseController {

    @Resource
    private IFacilityBuildingService buildingService;
    @Resource
    private IFacilityRoomService roomService;
    @Resource
    private IFacilityBookingService bookingService;
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

    // 楼房列表（仅正常）
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/building/list")
    public TableDataInfo buildings(@RequestParam(required = false) String keyword) {
        startPage();
        FacilityBuilding q = new FacilityBuilding();
        q.setStatus("0");
        if (keyword != null && !keyword.isEmpty()) q.setBuildingName(keyword);
        List<FacilityBuilding> list = buildingService.selectList(q);
        return getDataTable(list);
    }

    // 房间列表（仅启用）
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/list")
    public TableDataInfo rooms(@RequestParam Long buildingId, @RequestParam Integer floorNo) {
        startPage();
        FacilityRoom q = new FacilityRoom();
        q.setBuildingIdEq(buildingId);
        q.setFloorNoEq(floorNo);
        q.setStatus("0");
        List<FacilityRoom> list = roomService.selectList(q);
        return getDataTable(list);
    }

    // 房间详情
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/{id}")
    public AjaxResult roomInfo(@PathVariable Long id) {
        return success(roomService.selectById(id));
    }

    // 时间轴（近30天为默认范围）
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/{id}/timeline")
    public AjaxResult timeline(@PathVariable Long id,
                               @RequestParam(required = false) Date from,
                               @RequestParam(required = false) Date to) {
        List<TimelineSegmentVO> segs = bookingService.timeline(id, from, to);
        HashMap<String, Object> res = new HashMap<>();
        res.put("roomId", id);
        HashMap<String, Object> range = new HashMap<>();
        range.put("from", from);
        range.put("to", to);
        res.put("range", range);
        res.put("segments", segs);
        return success(res);
    }

    // 创建预约
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/booking")
    public AjaxResult create(@RequestBody FacilityBookingCreateRequest body) {
        FacilityBooking data = new FacilityBooking();
        data.setRoomId(body.getRoomId());
        data.setPurpose(body.getPurpose());
        data.setStartTime(body.getStartTime());
        data.setEndTime(body.getEndTime());
        int n = bookingService.create(data, body.getUserIdList(), getUserId(), getUsername());
        return toAjax(n);
    }

    // 我的预约
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/booking/my/list")
    public TableDataInfo myList(@RequestParam(required = false) String status) {
        startPage();
        List<FacilityBooking> list = bookingService.myList(getUserId(), status);
        // 友好化：补充房间名称，便于前端展示
        if (list != null && !list.isEmpty()) {
            java.util.Map<Long, String> roomNameMap = new java.util.HashMap<>();
            for (FacilityBooking b : list) {
                Long rid = b.getRoomId();
                if (rid == null) continue;
                String rn = roomNameMap.get(rid);
                if (rn == null) {
                    try {
                        FacilityRoom r = roomMapper.selectById(rid);
                        rn = r != null ? r.getRoomName() : null;
                    } catch (Exception ignored) {
                        rn = null;
                    }
                    roomNameMap.put(rid, rn);
                }
                b.setRoomName(rn);
            }
        }
        return getDataTable(list);
    }

    // 修改预约（开始前）
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/booking/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody FacilityBookingUpdateRequest body) {
        FacilityBooking patch = new FacilityBooking();
        patch.setPurpose(body.getPurpose());
        patch.setStartTime(body.getStartTime());
        patch.setEndTime(body.getEndTime());
        int n = bookingService.updateBeforeStart(id, patch, body.getUserIdList(), getUserId(), getUsername());
        return toAjax(n);
    }

    // 进行中提前结束
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/booking/{id}/end-early")
    public AjaxResult endEarly(@PathVariable Long id, @RequestBody FacilityBookingEndEarlyRequest body) {
        int n = bookingService.endEarly(id, body.getEndTime(), getUserId(), getUsername());
        return toAjax(n);
    }

    // 取消预约（开始前）
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/booking/{id}")
    public AjaxResult cancel(@PathVariable Long id) {
        return toAjax(bookingService.cancel(id, getUserId()));
    }

    // 预约详情（仅展示信息，不包含管理动作）
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/booking/{id}")
    public AjaxResult bookingInfo(@PathVariable Long id) {
        com.ruoyi.manage.domain.FacilityBooking b = bookingMapper.selectById(id);
        if (b == null) return error("记录不存在");
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        // 仅返回基础信息 + 友好信息
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
        } catch (Exception ignored) {
        }
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
        } catch (Exception ignored) {
        }
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
