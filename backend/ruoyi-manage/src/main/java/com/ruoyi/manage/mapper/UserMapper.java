package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.User;

/**
 * 卖家用户 Mapper（示例）
 */
public interface UserMapper {
    public List<User> selectUserList(User user);

    public int deleteUserById(Long userId);
}
