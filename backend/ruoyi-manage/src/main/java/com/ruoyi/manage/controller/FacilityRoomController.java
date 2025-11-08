package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FacilityRoom;
import com.ruoyi.manage.service.IFacilityBookingService;
import com.ruoyi.manage.service.IFacilityRoomService;
import com.ruoyi.manage.vo.TimelineSegmentVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/facility/room")
public class FacilityRoomController extends BaseController {

    @Autowired
    private IFacilityRoomService service;
    @Autowired
    private IFacilityBookingService bookingService;

    @PreAuthorize("@ss.hasPermi('manage:facility:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Long buildingId,
                              @RequestParam(required = false) Integer floorNo,
                              @RequestParam(required = false) String status) {
        FacilityRoom q = new FacilityRoom();
        q.setBuildingIdEq(buildingId);
        if (floorNo != null) {
            q.setFloorNoEq(floorNo);
        }
        // 管理端：不强制仅返回启用；若传入 status 则按传入过滤
        if (status != null && !status.trim().isEmpty()) {
            q.setStatus(status.trim());
        }
        startPage();
        List<FacilityRoom> list = service.selectList(q);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:room:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:room:timeline')")
    @GetMapping("/{id}/timeline")
    public AjaxResult timeline(@PathVariable Long id,
                               @RequestParam(required = false) Date from,
                               @RequestParam(required = false) Date to) {
        List<TimelineSegmentVO> segs = bookingService.timeline(id, from, to);
        return success(new java.util.HashMap<String, Object>() {{
            put("roomId", id);
            put("range", new java.util.HashMap<String, Object>() {{
                put("from", from);
                put("to", to);
            }});
            put("segments", segs);
        }});
    }

    // 管理端 CRUD
    @Log(title = "功能房-房间", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:facility:room:add')")
    @PostMapping
    public AjaxResult add(@RequestBody FacilityRoom data) {
        if (data.getStatus() == null || data.getStatus().trim().isEmpty()) {
            data.setStatus("0");
        }
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "功能房-房间", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:facility:room:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody FacilityRoom data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @Log(title = "功能房-房间启禁", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:facility:room:enable')")
    @PutMapping("/{id}/enable")
    public AjaxResult enable(@PathVariable Long id, @RequestParam Boolean enable) {
        return toAjax(service.enable(id, Boolean.TRUE.equals(enable)));
    }

    @Log(title = "功能房-房间", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:facility:room:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }
}
