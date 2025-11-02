package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class NoticeRead extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long noticeId;
    private Long userId;
    private Integer ack; // 0/1（预留）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
    private String delFlag; // 0/2
}

