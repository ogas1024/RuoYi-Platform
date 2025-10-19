package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class Major extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String majorName;
    private String status; // 0正常 1停用
    private String remark;
    private String delFlag; // 0存在 2删除
}

