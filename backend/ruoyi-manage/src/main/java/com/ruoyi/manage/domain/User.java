package com.ruoyi.manage.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long roleId;
    private String userName;
    private String password;
    private String status;
    private String nickName;
    private String roleName;
}
