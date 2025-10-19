package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.Course;
import com.ruoyi.manage.mapper.CourseMapper;
import com.ruoyi.manage.service.ICourseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    @Override
    public Course selectCourseById(Long id) {
        return courseMapper.selectCourseById(id);
    }

    @Override
    public List<Course> selectCourseList(Course course) {
        // 专业负责人仅查看本专业课程
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return courseMapper.selectMyLeadCourseList(SecurityUtils.getUserId(), course);
        }
        return courseMapper.selectCourseList(course);
    }

    @Override
    public int insertCourse(Course course) {
        return courseMapper.insertCourse(course);
    }

    @Override
    public int updateCourse(Course course) {
        return courseMapper.updateCourse(course);
    }

    @Override
    public int deleteCourseByIds(Long[] ids) {
        return courseMapper.deleteCourseByIds(ids);
    }
}

