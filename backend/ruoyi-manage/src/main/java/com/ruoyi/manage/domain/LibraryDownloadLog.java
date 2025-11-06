package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LibraryDownloadLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private Long assetId;
    private Long userId;
    private String result; // 0成功 1失败
    private String ip;
    private String ua;
    private String referer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
