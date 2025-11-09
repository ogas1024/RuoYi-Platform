package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Library extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** ISBN-13（13位，含校验位） */
    private String isbn13;
    /** 书名 */
    private String title;
    /** 作者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 出版年份 */
    private Integer publishYear;
    /** 语言（zh/en/...） */
    private String language;
    /** 关键字（分词或逗号分隔） */
    private String keywords;
    /** 摘要简介 */
    private String summary;
    /** 封面图片URL */
    private String coverUrl;
    /** 状态：0待审 1已上架 2驳回 3已下架 */
    private Integer status; // 0待审 1已通过 2驳回 3已下架
    /** 审核人 */
    private String auditBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 审核时间 */
    private Date auditTime;
    /** 驳回/下架原因 */
    private String auditReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 上架时间 */
    private Date publishTime;
    /** 下载次数 */
    private Long downloadCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 最近下载时间 */
    private Date lastDownloadTime;
    /** 上传者ID */
    private Long uploaderId;
    /** 上传者用户名 */
    private String uploaderName;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 以下为查询辅助字段（不入库）：
    /**
     * 关键字搜索：匹配 title/author/isbn13/keywords
     */
    /** 关键字搜索（匹配 title/author/isbn13/keywords） */
    private String keyword;
    /**
     * 筛选指定资产格式（pdf/epub/mobi/zip），基于资产表 exists
     */
    /** 资产格式筛选（pdf/epub/mobi/zip） */
    private String format;
}
