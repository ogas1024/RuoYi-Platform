package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.service.IMajorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/portal/major")
public class PortalMajorController extends BaseController {

    @Autowired
    private IMajorService majorService;

    /**
     * 门户专业列表
     * 路径：GET /portal/major/list
     * 权限：默认放开或网关鉴权；如需登录可加 isAuthenticated
     * 说明：仅返回正常状态（status=0）。
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @GetMapping("/list")
    public TableDataInfo list(Major query) {
        // 仅展示正常状态
        query.setStatus("0");
        startPage();
        List<Major> list = majorService.selectMajorList(query);
        return getDataTable(list);
    }
}
