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
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/portal/facility")
public class PortalFacilityController extends BaseController {

    @Autowired
    private IFacilityBuildingService buildingService;
    @Autowired
    private IFacilityRoomService roomService;
    @Autowired
    private IFacilityBookingService bookingService;

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
        List<FacilityBooking> list = bookingService.myListWithRoomName(getUserId(), status);
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
        return success(bookingService.getDetailWithMeta(id));
    }
}
