package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class CrUserScoreLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 专业ID（0=全站时也记录对应专业ID） */
    private Long majorId; // 0=全站时也记录对应专业ID
    /** 资源ID（触发积分的课程资源） */
    private Long resourceId;
    /** 事件类型：APPROVE/BEST */
    private String eventType; // APPROVE/BEST
    /** 分值增量（正整数） */
    private Integer delta;
    /** 删除标志 */
    private String delFlag;
}
