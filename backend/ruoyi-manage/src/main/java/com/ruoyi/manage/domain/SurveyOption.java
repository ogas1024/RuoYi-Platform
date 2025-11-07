package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyOption extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long itemId;
    private String label;
    private String value;
    private Integer sortNo;
    private String delFlag;
}

