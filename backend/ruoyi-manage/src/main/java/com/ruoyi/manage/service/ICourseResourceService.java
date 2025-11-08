package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.domain.vo.PieItem;

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

    /**
     * 上传趋势（按日统计 create_time），默认近30天。
     * @param days 天数（含今天），例如 30 表示近30天。
     */
    java.util.List<DayCount> uploadTrend(Integer days);

    /**
     * 下载趋势（按日统计日志 DOWNLOAD），默认近30天。
     * @param days 天数（含今天）
     */
    java.util.List<DayCount> downloadTrend(Integer days);

    /** 课程资源：用户下载TOP榜 */
    java.util.List<TopUserVO> topDownloadUsers(Integer limit);

    /** 专业占比（默认近30天） */
    java.util.List<PieItem> majorShare(Integer days);

    /** 课程占比（默认近30天） */
    java.util.List<PieItem> courseShare(Integer days);
}
