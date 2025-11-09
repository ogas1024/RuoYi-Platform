package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilityBookingUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 预约ID */
    private Long bookingId;
    /** 用户ID（参与人） */
    private Long userId;
    /** 是否为申请人：1是 0否 */
    private String isApplicant; // 1是 0否
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
}
