package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FacilityBuilding;
import com.ruoyi.manage.domain.FacilityRoom;
import com.ruoyi.manage.service.IFacilityBuildingService;
import com.ruoyi.manage.service.IFacilityBookingService;
import com.ruoyi.manage.service.IFacilityRoomService;
import com.ruoyi.manage.vo.TimelineSegmentVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/building")
public class FacilityBuildingController extends BaseController {

    @Autowired
    private IFacilityBuildingService service;
    @Autowired
    private IFacilityRoomService roomService;
    @Autowired
    private IFacilityBookingService bookingService;

    @PreAuthorize("@ss.hasPermi('manage:facility:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(FacilityBuilding query) {
        startPage();
        List<FacilityBuilding> list = service.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:building:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:add')")
    @PostMapping
    public AjaxResult add(@RequestBody FacilityBuilding data) {
        if (data.getStatus() == null || data.getStatus().trim().isEmpty()) {
            data.setStatus("0");
        }
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody FacilityBuilding data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @Log(title = "功能房-楼房", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:facility:building:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }

    /**
     * 楼房甘特图：聚合该楼房下所有房间的时间轴
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:building:gantt')")
    @GetMapping("/{id}/gantt")
    public AjaxResult gantt(@PathVariable Long id,
                            @RequestParam(required = false) java.util.Date from,
                            @RequestParam(required = false) java.util.Date to,
                            @RequestParam(required = false) String status) {
        // 查询该楼房下的房间（默认仅展示启用房间，可通过 status 参数覆盖）
        FacilityRoom q = new FacilityRoom();
        q.setBuildingIdEq(id);
        if (status != null && !status.trim().isEmpty()) {
            // 仅当显式传入 status 时才过滤；默认不过滤，避免漏掉禁用房间的历史预约
            q.setStatus(status.trim());
        }
        java.util.List<FacilityRoom> rooms = roomService.selectList(q);

        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
        for (FacilityRoom r : rooms) {
            java.util.List<TimelineSegmentVO> segs = bookingService.timeline(r.getId(), from, to);
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("roomId", r.getId());
            m.put("roomName", r.getRoomName());
            m.put("floorNo", r.getFloorNo());
            m.put("segments", segs);
            items.add(m);
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("buildingId", id);
        java.util.Map<String, Object> range = new java.util.HashMap<>();
        range.put("from", from);
        range.put("to", to);
        data.put("range", range);
        data.put("items", items);
        return success(data);
    }
}
