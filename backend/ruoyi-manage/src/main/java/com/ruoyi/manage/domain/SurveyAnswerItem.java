package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyAnswerItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long answerId;
    private Long itemId;
    private String valueText;
    private String valueOptionIds; // JSON 字符串（数组）
    private String delFlag;
}

