package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Course;

import java.util.List;

/**
 * 课程服务接口
 * 说明：对课程的基础增删改查封装，遵循 RuoYi 领域规范与审计字段约定。
 */
public interface ICourseService {
    /**
     * 按主键查询课程
     * @param id 课程ID
     * @return 课程实体，未找到返回 null
     */
    Course selectCourseById(Long id);

    /**
     * 查询课程列表
     * @param course 查询条件（名称、专业、状态等）
     * @return 课程集合
     */
    List<Course> selectCourseList(Course course);

    /**
     * 新增课程
     * @param course 实体（需补齐 createBy 等审计字段）
     * @return 影响行数
     */
    int insertCourse(Course course);

    /**
     * 修改课程
     * @param course 实体（需补齐 updateBy 等审计字段）
     * @return 影响行数
     */
    int updateCourse(Course course);

    /**
     * 批量删除课程
     * @param ids 课程ID数组
     * @return 影响行数
     */
    int deleteCourseByIds(Long[] ids);
}
