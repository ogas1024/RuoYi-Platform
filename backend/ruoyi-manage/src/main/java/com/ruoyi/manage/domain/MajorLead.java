package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class MajorLead extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long majorId;
    private Long userId;
    // 仅用于查询/提交：按用户名选择用户
    private String userName;
    // 展示字段
    private String nickName;
    private String remark;
    private String delFlag; // 0存在 2删除
    private String majorName;
}
