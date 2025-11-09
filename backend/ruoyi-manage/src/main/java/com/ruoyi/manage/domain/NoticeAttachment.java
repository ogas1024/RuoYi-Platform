package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class NoticeAttachment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 公告ID */
    private Long noticeId;
    /** 文件名 */
    private String fileName;
    /** 文件URL */
    private String fileUrl;
    /** 文件类型（MIME或扩展） */
    private String fileType;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 排序号（升序） */
    private Integer sort;
    /** 删除标志：0/2 */
    private String delFlag; // 0/2
}
