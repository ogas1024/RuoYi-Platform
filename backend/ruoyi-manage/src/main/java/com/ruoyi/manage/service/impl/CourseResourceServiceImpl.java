package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.domain.CourseResourceLog;
import com.ruoyi.manage.mapper.CourseResourceLogMapper;
import com.ruoyi.manage.mapper.CourseResourceMapper;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.service.IScoreService;
import com.ruoyi.manage.service.ICourseResourceService;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.domain.vo.PieItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.ruoyi.common.exception.ServiceException;

@Service
public class CourseResourceServiceImpl implements ICourseResourceService {

    @Autowired
    private CourseResourceMapper mapper;
    @Autowired
    private CourseResourceLogMapper logMapper;
    @Autowired
    private MajorLeadMapper majorLeadMapper;
    @Autowired
    private IScoreService scoreService;

    @Override
    public CourseResource selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<CourseResource> selectList(CourseResource query, boolean onlyApprovedForAnonymous) {
        if (onlyApprovedForAnonymous) {
            query.setStatus(1);
        }
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return mapper.selectMyLeadList(SecurityUtils.getUserId(), query);
        }
        return mapper.selectList(query);
    }

    @Override
    @Transactional
    public int insert(CourseResource data) {
        // 必填校验，避免落库约束报错
        if (data.getMajorId() == null) {
            throw new ServiceException("专业ID(majorId) 不能为空");
        }
        if (data.getCourseId() == null) {
            throw new ServiceException("课程ID(courseId) 不能为空");
        }
        if (data.getResourceName() == null || data.getResourceName().trim().isEmpty()) {
            throw new ServiceException("资源名称(resourceName) 不能为空");
        }
        if (data.getResourceType() == null) {
            throw new ServiceException("资源类型(resourceType) 不能为空：0-文件 1-外链");
        }
        if (data.getResourceType() == 0) { // 文件
            if (data.getFileUrl() == null || data.getFileUrl().trim().isEmpty()) {
                throw new ServiceException("文件型资源需要提供 fileUrl");
            }
            if (data.getFileHash() == null || data.getFileHash().trim().isEmpty()) {
                throw new ServiceException("文件型资源需要提供 fileHash（建议使用上传返回的哈希）");
            }
        } else if (data.getResourceType() == 1) { // 外链
            if (data.getLinkUrl() == null || data.getLinkUrl().trim().isEmpty()) {
                throw new ServiceException("外链型资源需要提供 linkUrl");
            }
        } else {
            throw new ServiceException("不支持的资源类型：" + data.getResourceType());
        }
        data.setCreateBy(SecurityUtils.getUsername());
        data.setCreateTime(DateUtils.getNowDate());
        data.setStatus(0); // 创建即待审
        try {
            int rows = mapper.insert(data);
            log("CREATE", data.getId(), "SUCCESS", null);
            return rows;
        } catch (DuplicateKeyException ex) {
            String msg;
            if (data.getResourceType() != null && data.getResourceType() == 0) {
                msg = "同课程已存在相同文件（哈希重复），请勿重复上传";
            } else {
                msg = "同课程已存在相同外链（URL 重复），请勿重复提交";
            }
            throw new ServiceException(msg);
        }
    }

    @Override
    @Transactional
    public int update(CourseResource data) {
        // 非管理员/专业负责人：仅允许编辑本人资源，且已通过需先下架
        CourseResource origin = mapper.selectById(data.getId());
        if (origin == null) throw new ServiceException("资源不存在: " + data.getId());
        boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        boolean isLead = SecurityUtils.hasRole("major_lead");
        if (!isAdmin && !isLead) {
            if (origin.getUploaderId() == null || !origin.getUploaderId().equals(SecurityUtils.getUserId())) {
                throw new ServiceException("无权限编辑他人资源");
            }
            if (origin.getStatus() != null && origin.getStatus() == 1) {
                throw new ServiceException("已通过资源请先下架后再编辑");
            }
        }
        // 任何编辑进入待审
        data.setStatus(0);
        data.setUpdateBy(SecurityUtils.getUsername());
        data.setUpdateTime(DateUtils.getNowDate());
        int rows = mapper.update(data);
        log("EDIT", data.getId(), "SUCCESS", null);
        return rows;
    }

    @Override
    @Transactional
    public int deleteByIds(Long[] ids, Long currentUserId, boolean isAdminOrLead) {
        int total = 0;
        for (Long id : ids) {
            CourseResource r = mapper.selectById(id);
            if (r == null) {
                throw new ServiceException("资源不存在: " + id);
            }
            if (!isAdminOrLead) {
                // 仅允许删除本人 且 状态 ∈ 待审(0)/驳回(2)/已下架(3)
                if (r.getUploaderId() == null || !r.getUploaderId().equals(currentUserId)) {
                    throw new ServiceException("无权限删除他人资源");
                }
                if (r.getStatus() != null && r.getStatus() == 1) {
                    throw new ServiceException("已通过资源请先下架或等待审核");
                }
            } else if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
                // 负责人仅能删除其负责专业下的资源
                Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
                if (ok == null || ok == 0) {
                    throw new ServiceException("无权删除该专业下的资源: " + id);
                }
            }
            total += mapper.deleteById(id);
            log("DELETE", id, "SUCCESS", null);
        }
        return total;
    }

    @Override
    @Transactional
    public int approve(Long id, String auditor) {
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            CourseResource r = mapper.selectById(id);
            if (r == null) throw new ServiceException("资源不存在: " + id);
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权审核该专业资源");
        }
        int rows = mapper.approve(id, auditor, DateUtils.getNowDate());
        if (rows > 0) {
            log("APPROVE", id, "SUCCESS", null);
            // 审核通过首次加分（幂等由流水唯一约束保证）
            CourseResource r = mapper.selectById(id);
            scoreService.awardApprove(r, auditor);
        }
        return rows;
    }

    @Override
    @Transactional
    public int reject(Long id, String auditor, String reason) {
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            CourseResource r = mapper.selectById(id);
            if (r == null) throw new ServiceException("资源不存在: " + id);
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权审核该专业资源");
        }
        int rows = mapper.reject(id, auditor, DateUtils.getNowDate(), reason);
        if (rows > 0) log("REJECT", id, "SUCCESS", reason);
        return rows;
    }

    @Override
    @Transactional
    public int offline(Long id, String auditor, String reason) {
        CourseResource r = mapper.selectById(id);
        if (r == null) throw new ServiceException("资源不存在: " + id);
        boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        boolean isLead = SecurityUtils.hasRole("major_lead");
        if (isLead && !isAdmin) {
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权下架该专业资源");
        }
        if (!isAdmin && !isLead) {
            if (r.getUploaderId() == null || !r.getUploaderId().equals(SecurityUtils.getUserId())) {
                throw new ServiceException("无权限下架他人资源");
            }
        }
        int rows = mapper.offline(id, auditor, DateUtils.getNowDate(), reason);
        if (rows > 0) {
            log("OFFLINE", id, "SUCCESS", reason);
            // 下架时自动取消“最佳”（如有）
            int unbest = mapper.unsetBest(id);
            if (unbest > 0) {
                log("UNBEST", id, "SUCCESS", "autoByOffline");
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public int onlineToPending(Long id) {
        CourseResource r = mapper.selectById(id);
        if (r == null) throw new ServiceException("资源不存在: " + id);
        boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        boolean isLead = SecurityUtils.hasRole("major_lead");
        if (isLead && !isAdmin) {
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权操作该专业资源");
        }
        if (!isAdmin && !isLead) {
            if (r.getUploaderId() == null || !r.getUploaderId().equals(SecurityUtils.getUserId())) {
                throw new ServiceException("无权限上架他人资源");
            }
        }
        int rows = mapper.onlineToPending(id);
        if (rows > 0) log("ONLINE", id, "SUCCESS", null);
        return rows;
    }

    @Override
    @Transactional
    public int incrDownload(Long id) {
        int rows = mapper.incrDownload(id, DateUtils.getNowDate());
        if (rows > 0) log("DOWNLOAD", id, "SUCCESS", null);
        return rows;
    }

    @Override
    public List<CourseResource> selectTop(String scope, Long majorId, Long courseId, Integer days, Integer limit) {
        Date fromTime = null;
        if (days != null && days > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -days);
            fromTime = cal.getTime();
        }
        return mapper.selectTop(scope, majorId, courseId, fromTime, limit);
    }

    @Override
    @Transactional
    public int setBest(Long id, String operator) {
        CourseResource r = mapper.selectById(id);
        if (r == null) throw new ServiceException("资源不存在: " + id);
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权标记本专业以外资源为最佳");
        }
        int rows = mapper.setBest(id, operator, DateUtils.getNowDate());
        if (rows > 0) {
            log("BEST", id, "SUCCESS", null);
            // 首次设置最佳加分
            r = mapper.selectById(id);
            scoreService.awardBest(r, operator);
        }
        return rows;
    }

    @Override
    @Transactional
    public int unsetBest(Long id) {
        CourseResource r = mapper.selectById(id);
        if (r == null) throw new ServiceException("资源不存在: " + id);
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权取消本专业以外资源的最佳");
        }
        int rows = mapper.unsetBest(id);
        if (rows > 0) log("UNBEST", id, "SUCCESS", null);
        return rows;
    }

    private void log(String action, Long resourceId, String result, String detail) {
        CourseResourceLog log = new CourseResourceLog();
        log.setResourceId(resourceId);
        log.setAction(action);
        Long actorId = null;
        String actorName = "portal";
        try {
            actorId = SecurityUtils.getUserId();
        } catch (Exception ignored) {
        }
        try {
            actorName = SecurityUtils.getUsername();
        } catch (Exception ignored) {
        }
        log.setActorId(actorId);
        log.setActorName(actorName);
        log.setIp(null);
        log.setUserAgent(null);
        log.setDetail(detail);
        log.setResult(result);
        log.setCreateBy(actorName);
        log.setCreateTime(DateUtils.getNowDate());
        logMapper.insert(log);
    }

    @Override
    public List<DayCount> uploadTrend(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 7 : days;
        // [from, to)：from 为 d-1 天前 00:00:00；to 为 明天 00:00:00（包含今天整天）
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());

        List<DayCount> raw = mapper.selectUploadCountByDay(from, to);
        HashMap<String, Long> map = new HashMap<>();
        if (raw != null) {
            for (DayCount dc : raw) {
                if (dc != null && dc.getDay() != null) {
                    map.put(dc.getDay(), dc.getCount() == null ? 0L : dc.getCount());
                }
            }
        }
        ArrayList<DayCount> result = new ArrayList<>();
        for (java.time.LocalDate cursor = fromDate; cursor.isBefore(toDate); cursor = cursor.plusDays(1)) {
            String key = cursor.toString();
            DayCount dc = new DayCount();
            dc.setDay(key);
            dc.setCount(map.getOrDefault(key, 0L));
            result.add(dc);
        }
        return result;
    }

    @Override
    public List<DayCount> downloadTrend(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 7 : days;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());

        List<DayCount> raw = logMapper.selectDownloadCountByDay(from, to);
        HashMap<String, Long> map = new HashMap<>();
        if (raw != null) {
            for (DayCount dc : raw) {
                if (dc != null && dc.getDay() != null) {
                    map.put(dc.getDay(), dc.getCount() == null ? 0L : dc.getCount());
                }
            }
        }
        ArrayList<DayCount> result = new ArrayList<>();
        for (java.time.LocalDate cursor = fromDate; cursor.isBefore(toDate); cursor = cursor.plusDays(1)) {
            String key = cursor.toString();
            DayCount dc = new DayCount();
            dc.setDay(key);
            dc.setCount(map.getOrDefault(key, 0L));
            result.add(dc);
        }
        return result;
    }

    @Override
    public java.util.List<TopUserVO> topDownloadUsers(Integer limit) {
        int n = (limit == null || limit <= 0 || limit > 100) ? 5 : limit;
        return logMapper.selectTopDownloadUsers(n);
    }

    @Override
    public java.util.List<PieItem> majorShare(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 30 : days;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        java.util.Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        java.util.Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());
        return mapper.selectMajorShare(from, to);
    }

    @Override
    public java.util.List<PieItem> courseShare(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 30 : days;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        java.util.Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        java.util.Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());
        return mapper.selectCourseShare(from, to);
    }

    @Override
    public java.util.List<PieItem> majorShareAll() {
        // 全站全时段：不限制时间窗口
        return mapper.selectMajorShare(null, null);
    }

    @Override
    public java.util.List<PieItem> courseShareAll() {
        return mapper.selectCourseShare(null, null);
    }

    @Override
    public boolean userHasMajorLeadAccess(Long userId, Long majorId) {
        if (userId == null || majorId == null) return false;
        try {
            Integer ok = majorLeadMapper.existsUserMajor(userId, majorId);
            return ok != null && ok > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
