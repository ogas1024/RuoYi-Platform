package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long surveyId;
    private String title;
    private Integer type; // 1文本 2单选 3多选（预留4文件 5时间）
    private Integer required; // 0/1
    private Integer sortNo;
    private String extJson; // 预留
    private String delFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SurveyOption> options;
}

