package com.ruoyi.manage.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.mapper.SysLinkageMapper;
import com.ruoyi.manage.service.IMajorLeadService;
import com.ruoyi.manage.vo.RoleUserLeadVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class MajorLeadServiceImpl implements IMajorLeadService {
    @Autowired
    private MajorLeadMapper mapper;
    @Autowired
    private SysLinkageMapper sysLinkageMapper;

    @Override
    public List<MajorLead> selectList(MajorLead query) {
        return mapper.selectList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(MajorLead data) {
        // 0) 校验用户存在
        Integer userExists = (data.getUserId() == null) ? 0 : sysLinkageMapper.existsUser(data.getUserId());
        if (userExists == null || userExists == 0) {
            throw new ServiceException("用户不存在或已被删除: " + data.getUserId());
        }
        // 1) 先插入映射（存在唯一键 (major_id, user_id)）
        int rows = mapper.insert(data);
        if (rows <= 0) {
            return rows;
        }
        // 2) 为该用户授予 RuoYi 角色：major_lead
        Long roleId = sysLinkageMapper.selectRoleIdByKey("major_lead");
        if (roleId == null) {
            throw new ServiceException("系统未配置角色标识 major_lead，请在‘系统管理-角色管理’中创建后重试");
        }
        Integer exists = sysLinkageMapper.existsUserRole(data.getUserId(), roleId);
        if (exists == null || exists == 0) {
            sysLinkageMapper.insertUserRole(data.getUserId(), roleId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        // 删除前收集受影响的用户ID
        List<Long> userIds = mapper.selectUserIdsByIds(ids);
        int rows = mapper.deleteByIds(ids);
        if (rows > 0 && userIds != null) {
            Long roleId = sysLinkageMapper.selectRoleIdByKey("major_lead");
            if (roleId != null) {
                for (Long uid : userIds) {
                    Integer rest = mapper.countByUserId(uid);
                    if (rest == null || rest == 0) {
                        // 无其它专业绑定，撤销角色
                        sysLinkageMapper.deleteUserRole(uid, roleId);
                    }
                }
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMajorAndUser(Long majorId, Long userId) {
        int rows = mapper.deleteByMajorAndUser(majorId, userId);
        if (rows > 0) {
            Long roleId = sysLinkageMapper.selectRoleIdByKey("major_lead");
            if (roleId != null) {
                Integer rest = mapper.countByUserId(userId);
                if (rest == null || rest == 0) {
                    sysLinkageMapper.deleteUserRole(userId, roleId);
                }
            }
        }
        return rows;
    }

    @Override
    public List<RoleUserLeadVO> listRoleUsers(Long majorId) {
        return sysLinkageMapper.selectRoleUsersWithMajors("major_lead", majorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int retireUser(Long userId) {
        int rows = mapper.deleteByUserId(userId);
        Long roleId = sysLinkageMapper.selectRoleIdByKey("major_lead");
        if (roleId != null) {
            sysLinkageMapper.deleteUserRole(userId, roleId);
        }
        return rows;
    }

    @Override
    public List<Major> listMyMajors(Long userId) {
        return mapper.selectMajorsByUserId(userId);
    }
}
