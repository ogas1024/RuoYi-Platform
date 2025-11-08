package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FacilityBan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId; // 申请人（入库用）
    // 展示与查询字段（非持久化）
    private String username;
    private String nickname;
    private String reason;
    private String status; // 0生效 1失效
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime; // 可选
    private String delFlag; // 0存在 2删除
}
