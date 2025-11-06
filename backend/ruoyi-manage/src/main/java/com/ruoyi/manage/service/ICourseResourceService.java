package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;

import java.util.Date;
import java.util.List;

public interface ICourseResourceService {
    CourseResource selectById(Long id);
    List<CourseResource> selectList(CourseResource query, boolean onlyApprovedForAnonymous);
    int insert(CourseResource data);
    int update(CourseResource data);
    int deleteByIds(Long[] ids, Long currentUserId, boolean isAdminOrLead);

    int approve(Long id, String auditor);
    int reject(Long id, String auditor, String reason);
    int offline(Long id, String auditor, String reason);
    int onlineToPending(Long id);
    int incrDownload(Long id);

    List<CourseResource> selectTop(String scope, Long majorId, Long courseId, Integer days, Integer limit);

    int setBest(Long id, String operator);
    int unsetBest(Long id);
}
