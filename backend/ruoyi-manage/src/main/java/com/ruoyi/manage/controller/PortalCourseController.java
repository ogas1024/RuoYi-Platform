package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Course;
import com.ruoyi.manage.service.ICourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/portal/course")
public class PortalCourseController extends BaseController {

    @Autowired
    private ICourseService courseService;

    /**
     * 门户课程列表
     * 路径：GET /portal/course/list
     * 权限：已登录（isAuthenticated 默认由网关控制；如需可加注解）
     * 说明：仅返回 status=0 的课程，支持分页通用参数。
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @GetMapping("/list")
    public TableDataInfo list(Course query) {
        query.setStatus("0");
        startPage();
        List<Course> list = courseService.selectCourseList(query);
        return getDataTable(list);
    }
}
