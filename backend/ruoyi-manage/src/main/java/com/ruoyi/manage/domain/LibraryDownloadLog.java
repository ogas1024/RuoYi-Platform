package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LibraryDownloadLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 图书ID */
    private Long bookId;
    /** 资产ID（文件/外链） */
    private Long assetId;
    /** 用户ID */
    private Long userId;
    /** 结果：0成功 1失败 */
    private String result; // 0成功 1失败
    /** 发起IP */
    private String ip;
    /** User-Agent */
    private String ua;
    /** Referer */
    private String referer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 记录时间 */
    private Date createTime;
}
