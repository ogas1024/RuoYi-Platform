package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.User;

public interface UserMapper
{
    public List<User> selectUserList(User user);
    public int deleteUserById(Long userId);
}
