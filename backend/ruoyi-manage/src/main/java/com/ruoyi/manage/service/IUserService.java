package com.ruoyi.manage.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.manage.domain.User;

import java.util.List;

public interface IUserService {
    public List<User> selectUserList(User user);
    public int deleteUserById(Long userId);
    public Long insertUser(User user);
    public boolean checkUserNameUnique(String userName,Long userId);
}
