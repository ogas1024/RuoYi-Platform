package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class CourseResourceLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long resourceId;
    private String action; // CREATE/EDIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE
    private Long actorId;
    private String actorName;
    private String ip;
    private String userAgent;
    private String detail; // JSON 文本
    private String result; // SUCCESS/FAIL
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 便于外显
    private String delFlag;
}

