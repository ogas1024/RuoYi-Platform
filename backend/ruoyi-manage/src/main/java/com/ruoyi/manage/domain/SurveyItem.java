package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 问卷ID */
    private Long surveyId;
    /** 题目标题 */
    private String title;
    /** 题目类型：1文本 2单选 3多选（预留4文件 5时间） */
    private Integer type; // 1文本 2单选 3多选（预留4文件 5时间）
    /** 是否必答：0/1 */
    private Integer required; // 0/1
    /** 排序号 */
    private Integer sortNo;
    /** 扩展配置（JSON，预留） */
    private String extJson; // 预留
    /** 删除标志 */
    private String delFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 选项列表（选择题） */
    private List<SurveyOption> options;
}
