package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Library extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String isbn13;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String language;
    private String keywords;
    private String summary;
    private String coverUrl;
    private Integer status; // 0待审 1已通过 2驳回 3已下架
    private String auditBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    private String auditReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    private Long downloadCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDownloadTime;
    private Long uploaderId;
    private String uploaderName;
    private String delFlag; // 0存在 2删除

    // 以下为查询辅助字段（不入库）：
    /** 关键字搜索：匹配 title/author/isbn13/keywords */
    private String keyword;
    /** 筛选指定资产格式（pdf/epub/mobi/zip），基于资产表 exists */
    private String format;
}
