package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CourseResource;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CourseResourceMapper {
    CourseResource selectById(Long id);
    List<CourseResource> selectList(CourseResource query);
    int insert(CourseResource data);
    int update(CourseResource data);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);

    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime);
    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);
    int offline(@Param("id") Long id);
    int onlineToPending(@Param("id") Long id);

    int incrDownload(@Param("id") Long id, @Param("time") Date time);

    List<CourseResource> selectTop(@Param("scope") String scope,
                                   @Param("majorId") Long majorId,
                                   @Param("courseId") Long courseId,
                                   @Param("fromTime") Date fromTime,
                                   @Param("limit") Integer limit);
}

