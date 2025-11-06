package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CourseResourceLog;
import com.ruoyi.manage.mapper.CourseResourceLogMapper;
import com.ruoyi.manage.mapper.CourseResourceMapper;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.service.IScoreService;
import com.ruoyi.manage.service.ICourseResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.exception.ServiceException;

@Service
public class CourseResourceServiceImpl implements ICourseResourceService {

    @Resource
    private CourseResourceMapper mapper;
    @Resource
    private CourseResourceLogMapper logMapper;
    @Resource
    private MajorLeadMapper majorLeadMapper;
    @Resource
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
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            CourseResource r = mapper.selectById(id);
            if (r == null) throw new ServiceException("资源不存在: " + id);
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权下架该专业资源");
        }
        int rows = mapper.offline(id, auditor, DateUtils.getNowDate(), reason);
        if (rows > 0) log("OFFLINE", id, "SUCCESS", reason);
        return rows;
    }

    @Override
    @Transactional
    public int onlineToPending(Long id) {
        if (SecurityUtils.hasRole("major_lead") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            CourseResource r = mapper.selectById(id);
            if (r == null) throw new ServiceException("资源不存在: " + id);
            Integer ok = majorLeadMapper.existsUserMajor(SecurityUtils.getUserId(), r.getMajorId());
            if (ok == null || ok == 0) throw new ServiceException("无权操作该专业资源");
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
        try { actorId = SecurityUtils.getUserId(); } catch (Exception ignored) {}
        try { actorName = SecurityUtils.getUsername(); } catch (Exception ignored) {}
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
}
