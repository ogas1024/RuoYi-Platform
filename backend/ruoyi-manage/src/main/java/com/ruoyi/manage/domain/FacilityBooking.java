package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FacilityBooking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long roomId;
    private Long applicantId;
    // 展示字段：申请人账号/昵称（审核列表）
    private String applicantUserName;
    private String applicantNickName;
    private String purpose;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer durationMinutes; // 30 的倍数
    private String status; // 0待审 1已批准 2已驳回 3已取消 4进行中 5已完成
    private String rejectReason;
    private String delFlag; // 0存在 2删除

    // 查询辅助字段
    private String statusEq;

    // 非持久化字段：前端友好展示
    private String roomName;
}
