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

    /**
     * 楼房列表（仅正常状态）
     * 路径：GET /portal/facility/building/list
     * 权限：已登录（isAuthenticated）
     * 说明：支持关键字按楼房名称模糊搜索，默认仅返回 status=0。
     *
     * @param keyword 关键词（可选）
     * @return 分页数据表格
     */
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

    /**
     * 房间列表（仅启用）
     * 路径：GET /portal/facility/room/list
     * 权限：已登录（isAuthenticated）
     * 说明：可按楼层号过滤；默认仅返回 status=0。
     *
     * @param buildingId 楼房ID
     * @param floorNo    楼层号（可选）
     * @return 分页数据表格
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/list")
    public TableDataInfo rooms(@RequestParam Long buildingId, @RequestParam(required = false) Integer floorNo) {
        startPage();
        FacilityRoom q = new FacilityRoom();
        q.setBuildingIdEq(buildingId);
        if (floorNo != null) {
            q.setFloorNoEq(floorNo);
        }
        q.setStatus("0");
        List<FacilityRoom> list = roomService.selectList(q);
        return getDataTable(list);
    }

    /**
     * 房间详情
     * 路径：GET /portal/facility/room/{id}
     * 权限：已登录（isAuthenticated）
     *
     * @param id 房间ID
     * @return 房间信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/{id}")
    public AjaxResult roomInfo(@PathVariable Long id) {
        return success(roomService.selectById(id));
    }

    /**
     * 房间时间轴（近30天为默认范围）
     * 路径：GET /portal/facility/room/{id}/timeline
     * 权限：已登录（isAuthenticated）
     * 说明：返回时间段片段列表及查询范围。
     *
     * @param id   房间ID
     * @param from 起始时间（可选）
     * @param to   截止时间（可选）
     * @return { roomId, range:{from,to}, segments }
     */
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

    /**
     * 创建预约
     * 路径：POST /portal/facility/booking
     * 权限：已登录（isAuthenticated）
     * 说明：校验房间状态/封禁/时间冲突/使用人等，由服务层处理；创建人为当前用户。
     *
     * @param body 创建请求
     * @return 操作结果
     */
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

    /**
     * 我的预约列表
     * 路径：GET /portal/facility/booking/my/list
     * 权限：已登录（isAuthenticated）
     * 说明：可按状态筛选。
     *
     * @param status 状态（可选）
     * @return 分页数据
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/booking/my/list")
    public TableDataInfo myList(@RequestParam(required = false) String status) {
        startPage();
        List<FacilityBooking> list = bookingService.myListWithRoomName(getUserId(), status);
        return getDataTable(list);
    }

    /**
     * 修改预约（开始前）
     * 路径：PUT /portal/facility/booking/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：仅在未开始前允许修改，包含冲突与时间窗口校验。
     *
     * @param id   预约ID
     * @param body 更新请求
     * @return 操作结果
     */
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

    /**
     * 提前结束（进行中）
     * 路径：PUT /portal/facility/booking/{id}/end-early
     * 权限：已登录（isAuthenticated）
     * 说明：结束时间不得早于当前时刻。
     *
     * @param id   预约ID
     * @param body 请求体（endTime）
     * @return 操作结果
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/booking/{id}/end-early")
    public AjaxResult endEarly(@PathVariable Long id, @RequestBody FacilityBookingEndEarlyRequest body) {
        int n = bookingService.endEarly(id, body.getEndTime(), getUserId(), getUsername());
        return toAjax(n);
    }

    /**
     * 取消预约（开始前）
     * 路径：DELETE /portal/facility/booking/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：仅申请人可取消且需未开始。
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/booking/{id}")
    public AjaxResult cancel(@PathVariable Long id) {
        return toAjax(bookingService.cancel(id, getUserId()));
    }

    /**
     * 预约详情
     * 路径：GET /portal/facility/booking/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：仅展示详情信息与关联元数据，不包含管理动作。
     *
     * @param id 预约ID
     * @return 详情及元信息
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/booking/{id}")
    public AjaxResult bookingInfo(@PathVariable Long id) {
        return success(bookingService.getDetailWithMeta(id));
    }
}
