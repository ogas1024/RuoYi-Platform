package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class CourseResource extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long majorId;
    private Long courseId;
    private String resourceName;
    private Integer resourceType; // 0-文件 1-外链
    private String fileUrl;
    private String fileHash;
    private Long fileSize;
    private String linkUrl;
    private String description;
    private Integer status; // 0待审 1通过 2驳回 3下架
    private Integer isBest; // 0否 1是
    private String bestBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bestTime;
    private String auditBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    private String auditReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    private Integer downloadCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDownloadTime;
    private Long uploaderId;
    private String uploaderName;
    private String delFlag; // 0存在 2删除

    // 冗余展示
    private String majorName;
    private String courseName;
}
