package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilitySetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String auditRequired; // 1是 0否
    private Integer maxDurationMinutes; // 全局上限，默认 4320
    private String remark;
    private String delFlag; // 0存在 2删除
}

