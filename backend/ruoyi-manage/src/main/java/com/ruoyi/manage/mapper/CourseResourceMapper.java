package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CourseResource;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.domain.vo.PieItem;

import java.util.Date;
import java.util.List;

public interface CourseResourceMapper {
    CourseResource selectById(Long id);

    List<CourseResource> selectList(CourseResource query);

    List<CourseResource> selectMyLeadList(@Param("userId") Long userId, @Param("query") CourseResource query);

    int insert(CourseResource data);

    int update(CourseResource data);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);

    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime);

    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    int onlineToPending(@Param("id") Long id);

    int incrDownload(@Param("id") Long id, @Param("time") Date time);

    List<CourseResource> selectTop(@Param("scope") String scope,
                                   @Param("majorId") Long majorId,
                                   @Param("courseId") Long courseId,
                                   @Param("fromTime") Date fromTime,
                                   @Param("limit") Integer limit);

    int setBest(@Param("id") Long id, @Param("bestBy") String bestBy, @Param("bestTime") Date bestTime);

    int unsetBest(@Param("id") Long id);

    /**
     * 统计：按天分组上传数量（create_time），仅统计未删除记录。
     *
     * @param from 起始（含）
     * @param to   截止（不含）
     */
    List<DayCount> selectUploadCountByDay(@Param("from") Date from, @Param("to") Date to);

    /**
     * 专业占比（按 create_time 过滤可选）
     */
    List<PieItem> selectMajorShare(@Param("from") Date from, @Param("to") Date to);

    /**
     * 课程占比（按 create_time 过滤可选）
     */
    List<PieItem> selectCourseShare(@Param("from") Date from, @Param("to") Date to);
}
