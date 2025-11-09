package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyScope extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 问卷ID */
    private Long surveyId;
    /** 范围类型：0角色 1部门 2岗位 */
    private Integer scopeType; // 0角色 1部门 2岗位
    /** 引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id */
    private Long refId;        // sys_role.role_id / sys_dept.dept_id / sys_post.post_id
    /** 删除标志：0/2 */
    private String delFlag;    // 0/2
}
