package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyScope extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long surveyId;
    private Integer scopeType; // 0角色 1部门 2岗位
    private Long refId;        // sys_role.role_id / sys_dept.dept_id / sys_post.post_id
    private String delFlag;    // 0/2
}

