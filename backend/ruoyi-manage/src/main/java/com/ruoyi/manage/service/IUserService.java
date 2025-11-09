package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.User;

import java.util.List;

/**
 * 卖家用户 服务接口（示例业务）
 */
public interface IUserService {
    /**
     * 查询用户列表
     * @param user 查询条件
     * @return 用户集合
     */
    List<User> selectUserList(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserById(Long userId);

    /**
     * 新增用户
     * 说明：需由调用方完成密码加密与审计字段填充。
     * @param user 实体
     * @return 生成的用户ID
     */
    Long insertUser(User user);

    /**
     * 校验登录名唯一
     * @param userName 登录名
     * @param userId   当前用户ID（编辑场景可传入做排除）
     * @return true 唯一 / false 已存在
     */
    boolean checkUserNameUnique(String userName, Long userId);
}
