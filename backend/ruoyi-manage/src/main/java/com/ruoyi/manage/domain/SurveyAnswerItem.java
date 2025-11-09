package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class SurveyAnswerItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 答卷ID */
    private Long answerId;
    /** 题目ID */
    private Long itemId;
    /** 文本答案（文本题） */
    private String valueText;
    /** 选项ID集合（JSON 数组，选择题） */
    private String valueOptionIds; // JSON 字符串（数组）
    /** 删除标志 */
    private String delFlag;
}
