package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class Major extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 专业名称（唯一） */
    private String majorName;
    /** 状态：0正常 1停用 */
    private String status; // 0正常 1停用
    /** 备注 */
    private String remark;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
}
