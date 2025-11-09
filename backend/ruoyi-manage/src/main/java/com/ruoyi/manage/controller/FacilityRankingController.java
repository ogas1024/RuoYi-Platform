package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.service.IFacilityRankingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/manage/facility/top")
public class FacilityRankingController extends BaseController {

    @Autowired
    private IFacilityRankingService service;

    /**
     * 房间使用排行
     * 路径：GET /manage/facility/top/room
     * 权限：manage:facility:top:room
     * 说明：period 支持 7d/30d 等窗口。
     *
     * @param period 统计窗口（默认7d）
     * @return 排行数据
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:top:room')")
    @GetMapping("/room")
    public AjaxResult room(@RequestParam(defaultValue = "7d") String period) {
        return success(service.rankRooms(period));
    }

    /**
     * 用户使用排行
     * 路径：GET /manage/facility/top/user
     * 权限：manage:facility:top:user
     *
     * @param period 统计窗口（默认7d）
     * @return 排行数据
     */
    @PreAuthorize("@ss.hasPermi('manage:facility:top:user')")
    @GetMapping("/user")
    public AjaxResult user(@RequestParam(defaultValue = "7d") String period) {
        return success(service.rankUsers(period));
    }
}
