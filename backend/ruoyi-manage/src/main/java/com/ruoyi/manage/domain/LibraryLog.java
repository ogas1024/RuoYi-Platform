package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LibraryLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private String action; // CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE
    private Long actorId;
    private String actorName;
    private String ip;
    private String ua;
    private String detail;
    private String result; // SUCCESS/FAIL
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String delFlag;
}
