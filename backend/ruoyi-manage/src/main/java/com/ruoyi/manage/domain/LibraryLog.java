package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LibraryLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 图书ID */
    private Long bookId;
    /** 行为：CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE */
    private String action; // CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE
    /** 操作人ID */
    private Long actorId;
    /** 操作人用户名 */
    private String actorName;
    /** 请求IP */
    private String ip;
    /** User-Agent */
    private String ua;
    /** 详情（JSON 文本） */
    private String detail;
    /** 结果：SUCCESS/FAIL */
    private String result; // SUCCESS/FAIL
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 创建时间 */
    private Date createTime;
    /** 删除标志 */
    private String delFlag;
}
