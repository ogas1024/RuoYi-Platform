package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilityBookingUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookingId;
    private Long userId;
    private String isApplicant; // 1是 0否
    private String delFlag; // 0存在 2删除
}

