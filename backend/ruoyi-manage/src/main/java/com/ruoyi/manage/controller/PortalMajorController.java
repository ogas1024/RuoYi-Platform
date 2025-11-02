package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.service.IMajorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/portal/major")
public class PortalMajorController extends BaseController {

    @Resource
    private IMajorService majorService;

    @GetMapping("/list")
    public TableDataInfo list(Major query) {
        // 仅展示正常状态
        query.setStatus("0");
        startPage();
        List<Major> list = majorService.selectMajorList(query);
        return getDataTable(list);
    }
}
