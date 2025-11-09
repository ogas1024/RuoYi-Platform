package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilitySetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID（固定1） */
    private Long id;
    /** 是否需要审核：1是 0否 */
    private String auditRequired; // 1是 0否
    /** 最长使用时长（分钟，全局上限，默认 4320） */
    private Integer maxDurationMinutes; // 全局上限，默认 4320
    /** 备注 */
    private String remark;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
}
