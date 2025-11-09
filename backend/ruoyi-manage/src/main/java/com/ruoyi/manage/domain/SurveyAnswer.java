package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SurveyAnswer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 问卷ID */
    private Long surveyId;
    /** 用户ID */
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 提交时间 */
    private Date submitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 更新时间（避免与审计字段重名） */
    private Date updateTime2; // 避免与审计字段重名
    /** 删除标志 */
    private String delFlag;
}
