package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Course;

import java.util.List;

public interface ICourseService {
    Course selectCourseById(Long id);
    List<Course> selectCourseList(Course course);
    int insertCourse(Course course);
    int updateCourse(Course course);
    int deleteCourseByIds(Long[] ids);
}

