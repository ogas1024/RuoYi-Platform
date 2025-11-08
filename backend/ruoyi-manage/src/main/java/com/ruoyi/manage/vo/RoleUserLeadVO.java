package com.ruoyi.manage.vo;

import lombok.Data;

/**
 * 拥有 major_lead 角色的用户视图（含其绑定的专业ID列表，逗号分隔）
 */
@Data
public class RoleUserLeadVO {
    private Long userId;
    private String userName;
    private String nickName;
    /**
     * 逗号分隔的专业ID列表，如 "1,2,5"
     */
    private String majorIds;
    /**
     * 逗号分隔的专业名列表，如 "计算机,数学"
     */
    private String majorNames;
}
