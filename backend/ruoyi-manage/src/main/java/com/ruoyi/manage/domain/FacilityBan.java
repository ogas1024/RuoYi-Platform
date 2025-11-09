package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FacilityBan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 被封禁用户ID（申请人） */
    private Long userId; // 申请人（入库用）
    // 展示与查询字段（非持久化）
    /** 用户名（展示/查询） */
    private String username;
    /** 昵称（展示/查询） */
    private String nickname;
    /** 封禁原因 */
    private String reason;
    /** 状态：0生效 1失效 */
    private String status; // 0生效 1失效
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 过期时间（可选，空表示永久） */
    private Date expireTime; // 可选
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
}
