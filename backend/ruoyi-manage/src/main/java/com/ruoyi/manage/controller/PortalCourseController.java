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

    @GetMapping("/list")
    public TableDataInfo list(Course query) {
        query.setStatus("0");
        startPage();
        List<Course> list = courseService.selectCourseList(query);
        return getDataTable(list);
    }
}
