package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class Course extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long majorId;
    private String courseName;
    private String courseCode;
    private String status; // 0正常 1停用
    private String remark;
    private String delFlag; // 0存在 2删除

    // 冗余展示
    private String majorName;
}

