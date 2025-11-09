package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.CrUserScore;
import com.ruoyi.manage.service.IScoreService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/score/user")
public class ScoreController extends BaseController {

    @Autowired
    private IScoreService scoreService;

    /**
     * 用户积分排行榜（管理端）
     * 路径：GET /manage/score/user/rank
     * 权限：manage:score:list
     * 说明：可按专业筛选；支持分页通用参数。
     *
     * @param majorId 专业ID（可选）
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:score:list')")
    @GetMapping("/rank")
    public TableDataInfo rank(@RequestParam(required = false) Long majorId) {
        startPage();
        List<CrUserScore> list = scoreService.selectRank(majorId);
        return getDataTable(list);
    }
}
