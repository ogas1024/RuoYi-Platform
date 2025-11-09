package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class LibraryLibrarian extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 用户ID（sys_user.user_id） */
    private Long userId;
    /** 用户名（冗余展示） */
    private String username;
    /** 昵称（冗余展示） */
    private String nickname;
    /** 备注 */
    private String remark;
    /** 删除标志 */
    private String delFlag;
}
