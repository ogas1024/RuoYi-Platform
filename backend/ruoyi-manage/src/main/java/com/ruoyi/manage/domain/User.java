package com.ruoyi.manage.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 主键ID（sys_user.user_id） */
    private Long userId;
    /** 角色ID（卖家对应角色） */
    private Long roleId;
    /** 登录账号 */
    private String userName;
    /** 登录密码（提交时传入，服务层加密） */
    private String password;
    /** 状态：0正常 1停用 */
    private String status;
    /** 昵称 */
    private String nickName;
    /** 角色名称（冗余） */
    private String roleName;
}
