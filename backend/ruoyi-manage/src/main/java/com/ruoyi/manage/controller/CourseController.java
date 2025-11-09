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

    /**
     * 课程列表
     * 路径：GET /manage/course/list
     * 权限：manage:course:list
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(Course query) {
        startPage();
        List<Course> list = courseService.selectCourseList(query);
        return getDataTable(list);
    }

    /**
     * 课程详情
     * 路径：GET /manage/course/{id}
     * 权限：manage:course:query
     *
     * @param id 课程ID
     * @return 详情
     */
    @PreAuthorize("@ss.hasPermi('manage:course:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(courseService.selectCourseById(id));
    }

    /**
     * 新增课程
     * 路径：POST /manage/course
     * 权限：manage:course:add
     *
     * @param course 实体
     * @return 操作结果
     */
    @Log(title = "课程", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:course:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Course course) {
        course.setCreateBy(getUsername());
        return toAjax(courseService.insertCourse(course));
    }

    /**
     * 编辑课程
     * 路径：PUT /manage/course
     * 权限：manage:course:edit
     *
     * @param course 实体
     * @return 操作结果
     */
    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:course:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Course course) {
        course.setUpdateBy(getUsername());
        return toAjax(courseService.updateCourse(course));
    }

    /**
     * 删除课程
     * 路径：DELETE /manage/course/{ids}
     * 权限：manage:course:remove
     *
     * @param ids ID数组
     * @return 操作结果
     */
    @Log(title = "课程", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:course:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(courseService.deleteCourseByIds(ids));
    }
}
