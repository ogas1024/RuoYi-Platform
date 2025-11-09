package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class CrUserScore extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 专业ID（0=全站积分） */
    private Long majorId; // 0=全站
    /** 总积分 */
    private Integer totalScore;
    /** 审核通过次数 */
    private Integer approveCount;
    /** 最佳标记次数 */
    private Integer bestCount;
    /** 删除标志 */
    private String delFlag;

    // 展示冗余
    /** 冗余：专业名称 */
    private String majorName;
    /** 排名（统计查询计算） */
    private Integer rank; // 窗口函数计算
}
