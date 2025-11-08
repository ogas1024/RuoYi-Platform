package com.ruoyi.manage.mapper;

import com.ruoyi.manage.vo.RoleUserLeadVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 跨模块访问 RuoYi 核心表（sys_user/sys_role/sys_user_role）的 Mapper
 */
public interface SysLinkageMapper {
    Long selectRoleIdByKey(@Param("roleKey") String roleKey);

    Integer existsUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    Integer existsUser(@Param("userId") Long userId);

    /**
     * 查询系统用户的账号与昵称
     */
    java.util.Map<String, Object> selectUserNameNickName(@Param("userId") Long userId);

    List<RoleUserLeadVO> selectRoleUsersWithMajors(@Param("roleKey") String roleKey, @Param("majorId") Long majorId);
}
