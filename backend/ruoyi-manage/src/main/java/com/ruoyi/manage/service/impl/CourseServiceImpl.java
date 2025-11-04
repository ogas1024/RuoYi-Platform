package com.ruoyi.manage.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.Course;
import com.ruoyi.manage.mapper.CourseMapper;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.service.ICourseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {

    @Resource
    private CourseMapper courseMapper;
    @Resource
    private MajorLeadMapper majorLeadMapper;

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
        // 负责人只能在其负责的专业下新增
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            if (course.getMajorId() == null) {
                throw new ServiceException("请选择专业");
            }
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), course.getMajorId());
            if (ok == null || ok == 0) {
                throw new ServiceException("无权在该专业下新增课程");
            }
        }
        return courseMapper.insertCourse(course);
    }

    @Override
    public int updateCourse(Course course) {
        // 负责人只能编辑其负责专业下的课程
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            Course exist = courseMapper.selectCourseById(course.getId());
            Long targetMajorId = course.getMajorId() != null ? course.getMajorId() : (exist != null ? exist.getMajorId() : null);
            if (targetMajorId == null) {
                throw new ServiceException("课程所属专业未知");
            }
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), targetMajorId);
            if (ok == null || ok == 0) {
                throw new ServiceException("无权编辑该专业下的课程");
            }
        }
        return courseMapper.updateCourse(course);
    }

    @Override
    public int deleteCourseByIds(Long[] ids) {
        // 负责人仅能删除其负责专业下的课程
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            for (Long id : ids) {
                Course c = courseMapper.selectCourseById(id);
                if (c == null) continue;
                Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), c.getMajorId());
                if (ok == null || ok == 0) {
                    throw new ServiceException("无权删除该专业下的课程: " + id);
                }
            }
        }
        return courseMapper.deleteCourseByIds(ids);
    }
}
