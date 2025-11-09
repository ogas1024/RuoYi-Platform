package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyOption extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 题目ID */
    private Long itemId;
    /** 选项展示文本 */
    private String label;
    /** 选项值（提交值） */
    private String value;
    /** 排序号 */
    private Integer sortNo;
    /** 删除标志 */
    private String delFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 统计：投票计数（管理详情返回） */
    private Integer voteCount; // 管理详情中返回的统计数
}
