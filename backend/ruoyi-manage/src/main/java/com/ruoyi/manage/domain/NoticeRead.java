package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class NoticeRead extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 公告ID */
    private Long noticeId;
    /** 用户ID */
    private Long userId;
    /** 已读确认（预留）：0/1 */
    private Integer ack; // 0/1（预留）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 阅读时间 */
    private Date readTime;
    /** 删除标志：0/2 */
    private String delFlag; // 0/2
}
