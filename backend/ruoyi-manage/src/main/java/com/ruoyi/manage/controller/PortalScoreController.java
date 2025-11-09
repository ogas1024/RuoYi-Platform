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

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/portal/score")
public class PortalScoreController extends BaseController {

    @Autowired
    private IScoreService scoreService;

    /**
     * 用户积分排行榜（门户）
     * 路径：GET /portal/score/rank
     * 权限：默认放开或网关鉴权；可根据需要限制为登录
     * 说明：可按专业筛选；支持分页通用参数。
     *
     * @param majorId 专业ID（可选）
     * @return 分页数据
     */
    @GetMapping("/rank")
    public TableDataInfo rank(@RequestParam(required = false) Long majorId) {
        startPage();
        List<CrUserScore> list = scoreService.selectRank(majorId);
        return getDataTable(list);
    }
}
