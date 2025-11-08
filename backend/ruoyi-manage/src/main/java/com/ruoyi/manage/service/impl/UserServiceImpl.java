package com.ruoyi.manage.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.User;
import com.ruoyi.manage.mapper.UserMapper;
import com.ruoyi.manage.service.IUserService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Override
    public List<User> selectUserList(User user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public int deleteUserById(Long userId) {
        return userMapper.deleteUserById(userId);
    }

    @Override
    public Long insertUser(User user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(user.getUserName());
        sysUser.setCreateBy(user.getCreateBy());
        if (StringUtils.isNotNull(user.getNickName())) {
            sysUser.setNickName(user.getNickName());
        } else {
            sysUser.setNickName(user.getUserName());
        }
        sysUser.setPassword(user.getPassword());
        sysUser.setStatus(user.getStatus());

        List<SysRole> roles = new ArrayList<>(1);
        SysRole role = new SysRole();
        role.setRoleId(user.getRoleId());
        sysUser.setRoles(roles);
        sysUser.setRoleIds(new Long[]{user.getRoleId()});

        // 新增用户信息
        sysUserMapper.insertUser(sysUser);
        // 新增用户与角色管理
        sysUserService.insertUserRole(sysUser);
        return sysUser.getUserId();
    }

    @Override
    public boolean checkUserNameUnique(String userName, Long userId) {
        userId = StringUtils.isNull(userId) ? -1L : userId;
        SysUser info = sysUserMapper.checkUserNameUnique(userName);
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
