package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class NoticeAttachment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long noticeId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Integer sort;
    private String delFlag; // 0/2
}

