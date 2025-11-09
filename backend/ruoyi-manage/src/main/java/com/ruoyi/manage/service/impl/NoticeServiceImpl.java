package com.ruoyi.manage.service.impl;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.Notice;
import com.ruoyi.manage.domain.NoticeAttachment;
import com.ruoyi.manage.domain.NoticeRead;
import com.ruoyi.manage.domain.NoticeScope;
import com.ruoyi.manage.mapper.NoticeAttachmentMapper;
import com.ruoyi.manage.mapper.NoticeMapper;
import com.ruoyi.manage.mapper.NoticeReadMapper;
import com.ruoyi.manage.mapper.NoticeScopeMapper;
import com.ruoyi.manage.service.INoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

/**
 * 通知公告 服务实现
 * 职责：公告的增删改查、发布/撤回/置顶、附件与范围管理、阅读回执记录。
 */
@Service
public class NoticeServiceImpl implements INoticeService {

    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private NoticeAttachmentMapper attachmentMapper;
    @Autowired
    private NoticeScopeMapper scopeMapper;
    @Autowired
    private NoticeReadMapper readMapper;

    @Override
    public List<Notice> list(Notice query) {
        Long userId = SecurityUtils.getUserId();
        Long deptId = SecurityUtils.getDeptId();
        boolean admin = SecurityUtils.isAdmin(userId);
        return noticeMapper.selectList(query, userId, deptId, admin ? 1 : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getDetailAndRecordRead(Long id) {
        Long userId = SecurityUtils.getUserId();
        Notice notice = noticeMapper.selectById(id, userId);
        if (notice == null || Objects.equals("2", notice.getDelFlag())) {
            throw new ServiceException("公告不存在或已删除");
        }

        // 可见性检查（管理员放行；非管理员需在可见范围内或全员可见，列表查询已覆盖，详情简单校验：如为草稿/撤回仅发布者或管理员可见）
        if (notice.getStatus() != null && notice.getStatus() != 1) {
            if (!SecurityUtils.isAdmin(userId) && !Objects.equals(userId, notice.getPublisherId())) {
                throw new ServiceException("无权查看该公告");
            }
        }

        // 聚合附件与范围
        List<NoticeAttachment> attachments = attachmentMapper.selectByNoticeId(id);
        List<NoticeScope> scopes = scopeMapper.selectByNoticeId(id);

        // 阅读回执（幂等）
        NoticeRead rec = new NoticeRead();
        rec.setNoticeId(id);
        rec.setUserId(userId);
        rec.setAck(0);
        rec.setReadTime(DateUtils.getNowDate());
        rec.setCreateBy(SecurityUtils.getUsername());
        rec.setCreateTime(DateUtils.getNowDate());
        rec.setUpdateBy(SecurityUtils.getUsername());
        rec.setUpdateTime(DateUtils.getNowDate());
        int inserted = readMapper.insertIgnore(rec);
        if (inserted > 0) {
            noticeMapper.incrReadCount(id);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("notice", notice);
        data.put("attachments", attachments);
        data.put("scopes", scopes);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Notice notice) {
        initForCreate(notice);
        notice.setStatus(notice.getStatus() == null ? 0 : notice.getStatus());
        notice.setPinned(0);
        notice.setEditCount(0);
        notice.setReadCount(0);
        notice.setAttachmentCount(notice.getAttachments() == null ? 0 : notice.getAttachments().size());
        int rows = noticeMapper.insert(notice);
        if (notice.getAttachments() != null) {
            for (NoticeAttachment a : notice.getAttachments()) {
                initChild(a);
                a.setNoticeId(notice.getId());
                attachmentMapper.insert(a);
            }
        }
        if (notice.getVisibleAll() != null && notice.getVisibleAll() == 0) {
            List<NoticeScope> scopes = normalizeScopes(notice.getScopes());
            if (scopes.isEmpty()) {
                throw new ServiceException("可见范围为自定义时，至少选择一个角色/部门/岗位");
            }
            for (NoticeScope s : scopes) {
                initChild(s);
                s.setNoticeId(notice.getId());
                scopeMapper.insert(s);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(Notice notice) {
        if (notice.getId() == null) {
            throw new ServiceException("ID 不能为空");
        }
        // 编辑次数+1，保持 publishTime 不变
        noticeMapper.incrEditCount(notice.getId());
        initForUpdate(notice);
        if (notice.getAttachments() != null) {
            attachmentMapper.deleteByNoticeId(notice.getId());
            for (NoticeAttachment a : notice.getAttachments()) {
                initChild(a);
                a.setNoticeId(notice.getId());
                attachmentMapper.insert(a);
            }
            notice.setAttachmentCount(notice.getAttachments().size());
        }
        if (notice.getVisibleAll() != null) {
            if (notice.getVisibleAll() == 1) {
                scopeMapper.deleteByNoticeId(notice.getId());
            } else {
                List<NoticeScope> scopes = normalizeScopes(notice.getScopes());
                if (scopes.isEmpty()) {
                    throw new ServiceException("可见范围为自定义时，至少选择一个角色/部门/岗位");
                }
                scopeMapper.deleteByNoticeId(notice.getId());
                for (NoticeScope s : scopes) {
                    initChild(s);
                    s.setNoticeId(notice.getId());
                    scopeMapper.insert(s);
                }
            }
        }
        return noticeMapper.update(notice);
    }

    @Override
    public int removeByIds(Long[] ids) {
        return noticeMapper.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publish(Long id) {
        // 幂等：已发布直接返回成功；不存在返回-404；其他非法状态返回-409
        Notice n = noticeMapper.selectById(id, null);
        if (n == null || Objects.equals("2", n.getDelFlag())) {
            return -404;
        }
        if (Objects.equals(1, n.getStatus())) {
            return 1;
        }
        return noticeMapper.publish(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int retract(Long id) {
        Notice n = noticeMapper.selectById(id, null);
        if (n == null || Objects.equals("2", n.getDelFlag())) {
            return -404;
        }
        if (!Objects.equals(1, n.getStatus())) {
            // 仅已发布可撤回；重复撤回视为状态冲突
            return -409;
        }
        return noticeMapper.retract(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pin(Long id, boolean pinned) {
        Notice n = noticeMapper.selectById(id, null);
        if (n == null || Objects.equals("2", n.getDelFlag())) {
            return -404;
        }
        if (pinned) {
            // 仅已发布可置顶
            if (!Objects.equals(1, n.getStatus())) {
                return -409;
            }
            return noticeMapper.pin(id);
        } else {
            // 取消置顶幂等，视为成功
            noticeMapper.unpin(id);
            return 1;
        }
    }

    private void initForCreate(Notice n) {
        n.setCreateBy(SecurityUtils.getUsername());
        n.setCreateTime(DateUtils.getNowDate());
        n.setUpdateBy(SecurityUtils.getUsername());
        n.setUpdateTime(DateUtils.getNowDate());
        n.setPublisherId(SecurityUtils.getUserId());
        if (n.getVisibleAll() == null) n.setVisibleAll(1);
        if (n.getType() == null) n.setType(2);
    }

    private void initForUpdate(Notice n) {
        n.setUpdateBy(SecurityUtils.getUsername());
        n.setUpdateTime(DateUtils.getNowDate());
    }

    private void initChild(Object obj) {
        if (obj instanceof NoticeAttachment) {
            NoticeAttachment a = (NoticeAttachment) obj;
            a.setCreateBy(SecurityUtils.getUsername());
            a.setCreateTime(DateUtils.getNowDate());
            a.setUpdateBy(SecurityUtils.getUsername());
            a.setUpdateTime(DateUtils.getNowDate());
        } else if (obj instanceof NoticeScope) {
            NoticeScope s = (NoticeScope) obj;
            s.setCreateBy(SecurityUtils.getUsername());
            s.setCreateTime(DateUtils.getNowDate());
            s.setUpdateBy(SecurityUtils.getUsername());
            s.setUpdateTime(DateUtils.getNowDate());
        }
    }

    /**
     * 规范化并过滤可见范围：
     * - 去除 null 项
     * - 仅保留 scopeType ∈ {0角色,1部门,2岗位} 且 refId 非空的项
     * - 对 (scopeType, refId) 进行去重以避免唯一约束冲突
     */
    private List<NoticeScope> normalizeScopes(List<NoticeScope> scopes) {
        if (scopes == null || scopes.isEmpty()) return Collections.emptyList();
        List<NoticeScope> list = new ArrayList<>();
        Set<String> dedup = new HashSet<>();
        for (NoticeScope s : scopes) {
            if (s == null) continue;
            Integer t = s.getScopeType();
            Long r = s.getRefId();
            if (r == null) continue;
            if (t == null || (t != 0 && t != 1 && t != 2)) continue;
            String key = t + ":" + r;
            if (dedup.add(key)) list.add(s);
        }
        return list;
    }
}
