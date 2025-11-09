package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper
 */
public interface CourseMapper {
    Course selectCourseById(Long id);

    /** 列表查询 */
    List<Course> selectCourseList(Course course);

    int insertCourse(Course course);

    int updateCourse(Course course);

    int deleteCourseById(Long id);

    int deleteCourseByIds(Long[] ids);

    /** 限制负责人范围的课程列表（专业负责人仅看自己专业） */
    List<Course> selectMyLeadCourseList(@Param("userId") Long userId, @Param("course") Course course);
}
