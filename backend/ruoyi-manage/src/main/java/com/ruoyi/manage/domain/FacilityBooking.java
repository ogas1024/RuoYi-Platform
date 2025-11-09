package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class FacilityBooking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 房间ID（tb_facility_room.id） */
    private Long roomId;
    /** 申请人用户ID */
    private Long applicantId;
    /** 展示字段：申请人账号（审核列表/详情） */
    private String applicantUserName;
    /** 展示字段：申请人昵称（审核列表/详情） */
    private String applicantNickName;
    /** 预约用途 */
    private String purpose;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 开始时间 */
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 结束时间 */
    private Date endTime;
    /** 持续时长（分钟，30 的倍数） */
    private Integer durationMinutes; // 30 的倍数
    /** 状态：0待审 1已批准 2已驳回 3已取消 4进行中 5已完成 */
    private String status; // 0待审 1已批准 2已驳回 3已取消 4进行中 5已完成
    /** 驳回原因 */
    private String rejectReason;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 查询辅助字段
    /** 查询等值状态（便捷筛选） */
    private String statusEq;

    // 非持久化字段：前端友好展示
    /** 展示字段：房间名称 */
    private String roomName;
}
