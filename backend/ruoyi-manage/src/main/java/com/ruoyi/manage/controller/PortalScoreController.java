package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.CrUserScore;
import com.ruoyi.manage.service.IScoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/portal/score")
public class PortalScoreController extends BaseController {

    @Resource
    private IScoreService scoreService;

    @GetMapping("/rank")
    public TableDataInfo rank(@RequestParam(required = false) Long majorId) {
        startPage();
        List<CrUserScore> list = scoreService.selectRank(majorId);
        return getDataTable(list);
    }
}

