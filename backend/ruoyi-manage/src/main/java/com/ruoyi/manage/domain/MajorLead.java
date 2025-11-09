package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class MajorLead extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 专业ID */
    private Long majorId;
    /** 用户ID */
    private Long userId;
    // 仅用于查询/提交：按用户名选择用户
    /** 查询/提交辅助：用户名 */
    private String userName;
    // 展示字段
    /** 展示：昵称 */
    private String nickName;
    /** 备注 */
    private String remark;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
    /** 冗余：专业名称 */
    private String majorName;
}
