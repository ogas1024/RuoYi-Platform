package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Course;
import com.ruoyi.manage.service.ICourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/manage/course")
public class CourseController extends BaseController {

    @Autowired
    private ICourseService courseService;

    @PreAuthorize("@ss.hasPermi('manage:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(Course query) {
        startPage();
        List<Course> list = courseService.selectCourseList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:course:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(courseService.selectCourseById(id));
    }

    @Log(title = "课程", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:course:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Course course) {
        course.setCreateBy(getUsername());
        return toAjax(courseService.insertCourse(course));
    }

    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:course:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Course course) {
        course.setUpdateBy(getUsername());
        return toAjax(courseService.updateCourse(course));
    }

    @Log(title = "课程", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:course:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(courseService.deleteCourseByIds(ids));
    }
}
