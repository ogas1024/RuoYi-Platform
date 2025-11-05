package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class CrUserScore extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private Long majorId; // 0=全站
    private Integer totalScore;
    private Integer approveCount;
    private Integer bestCount;
    private String delFlag;

    // 展示冗余
    private String majorName;
    private Integer rank; // 窗口函数计算
}

