package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class MajorLead extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long majorId;
    private Long userId;
    private String remark;
    private String delFlag; // 0存在 2删除
}

