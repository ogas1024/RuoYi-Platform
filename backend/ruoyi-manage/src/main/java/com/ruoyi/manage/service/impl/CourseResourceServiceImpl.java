package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CourseResourceLog;
import com.ruoyi.manage.mapper.CourseResourceLogMapper;
import com.ruoyi.manage.mapper.CourseResourceMapper;
import com.ruoyi.manage.service.ICourseResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public CourseResource selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<CourseResource> selectList(CourseResource query, boolean onlyApprovedForAnonymous) {
        if (onlyApprovedForAnonymous) {
            query.setStatus(1);
        }
        return mapper.selectList(query);
    }

    @Override
    @Transactional
    public int insert(CourseResource data) {
        data.setCreateBy(SecurityUtils.getUsername());
        data.setCreateTime(DateUtils.getNowDate());
        data.setStatus(0); // 创建即待审
        int rows = mapper.insert(data);
        log("CREATE", data.getId(), "SUCCESS", null);
        return rows;
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
            }
            total += mapper.deleteById(id);
            log("DELETE", id, "SUCCESS", null);
        }
        return total;
    }

    @Override
    @Transactional
    public int approve(Long id, String auditor) {
        int rows = mapper.approve(id, auditor, DateUtils.getNowDate());
        if (rows > 0) log("APPROVE", id, "SUCCESS", null);
        return rows;
    }

    @Override
    @Transactional
    public int reject(Long id, String auditor, String reason) {
        int rows = mapper.reject(id, auditor, DateUtils.getNowDate(), reason);
        if (rows > 0) log("REJECT", id, "SUCCESS", reason);
        return rows;
    }

    @Override
    @Transactional
    public int offline(Long id) {
        int rows = mapper.offline(id);
        if (rows > 0) log("OFFLINE", id, "SUCCESS", null);
        return rows;
    }

    @Override
    @Transactional
    public int onlineToPending(Long id) {
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

    private void log(String action, Long resourceId, String result, String detail) {
        CourseResourceLog log = new CourseResourceLog();
        log.setResourceId(resourceId);
        log.setAction(action);
        log.setActorId(SecurityUtils.getUserId());
        log.setActorName(SecurityUtils.getUsername());
        log.setIp(null);
        log.setUserAgent(null);
        log.setDetail(detail);
        log.setResult(result);
        log.setCreateBy(SecurityUtils.getUsername());
        log.setCreateTime(DateUtils.getNowDate());
        logMapper.insert(log);
    }
}
