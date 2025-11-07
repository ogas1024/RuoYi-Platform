package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SurveyAnswer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long surveyId;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime2; // 避免与审计字段重名
    private String delFlag;
}

