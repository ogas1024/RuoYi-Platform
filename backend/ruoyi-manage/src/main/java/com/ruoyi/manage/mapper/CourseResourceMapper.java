package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CourseResource;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.domain.vo.PieItem;

import java.util.Date;
import java.util.List;

/**
 * 课程资源 Mapper
 * 说明：管理课程资源主表与统计相关查询，含审核/上下架/下载与TOP榜。
 */
public interface CourseResourceMapper {
    CourseResource selectById(Long id);

    /**
     * 列表查询
     * @param query 条件（名称、专业、课程、状态等）
     */
    List<CourseResource> selectList(CourseResource query);

    /**
     * 按专业负责人可见范围查询（非管理员的 major_lead 仅看自己负责专业）
     */
    List<CourseResource> selectMyLeadList(@Param("userId") Long userId, @Param("query") CourseResource query);

    int insert(CourseResource data);

    int update(CourseResource data);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);

    /** 审核通过 */
    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime);

    /** 审核驳回 */
    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    /** 下架 */
    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    /** 提交上架（转待审） */
    int onlineToPending(@Param("id") Long id);

    /** 下载计数+1（记录日志） */
    int incrDownload(@Param("id") Long id, @Param("time") Date time);

    /**
     * TOP 榜查询
     * @param scope global/major/course
     */
    List<CourseResource> selectTop(@Param("scope") String scope,
                                   @Param("majorId") Long majorId,
                                   @Param("courseId") Long courseId,
                                   @Param("fromTime") Date fromTime,
                                   @Param("limit") Integer limit);

    /** 设为最佳 */
    int setBest(@Param("id") Long id, @Param("bestBy") String bestBy, @Param("bestTime") Date bestTime);

    /** 取消最佳 */
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
