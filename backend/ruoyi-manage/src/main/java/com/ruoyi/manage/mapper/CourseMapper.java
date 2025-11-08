package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {
    Course selectCourseById(Long id);

    List<Course> selectCourseList(Course course);

    int insertCourse(Course course);

    int updateCourse(Course course);

    int deleteCourseById(Long id);

    int deleteCourseByIds(Long[] ids);

    // 限制负责人范围
    List<Course> selectMyLeadCourseList(@Param("userId") Long userId, @Param("course") Course course);
}

