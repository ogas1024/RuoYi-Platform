package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class CrUserScoreLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private Long majorId; // 0=全站时也记录对应专业ID
    private Long resourceId;
    private String eventType; // APPROVE/BEST
    private Integer delta;
    private String delFlag;
}

