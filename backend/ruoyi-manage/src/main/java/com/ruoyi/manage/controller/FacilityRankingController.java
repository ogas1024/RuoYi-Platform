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

    @PreAuthorize("@ss.hasPermi('manage:facility:top:room')")
    @GetMapping("/room")
    public AjaxResult room(@RequestParam(defaultValue = "7d") String period) {
        return success(service.rankRooms(period));
    }

    @PreAuthorize("@ss.hasPermi('manage:facility:top:user')")
    @GetMapping("/user")
    public AjaxResult user(@RequestParam(defaultValue = "7d") String period) {
        return success(service.rankUsers(period));
    }
}
