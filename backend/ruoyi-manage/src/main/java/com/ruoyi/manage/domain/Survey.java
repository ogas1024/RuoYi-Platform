package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Survey extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 标题 */
    private String title;
    /** 状态：0草稿 1发布 2归档 */
    private Integer status; // 0草稿 1发布 2归档
    /** 是否全员可见：1是 0否（自定义范围） */
    private Integer visibleAll; // 1全员 0自定义
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 截止时间（可选） */
    private Date deadline; // 截止时间
    /** 备注 */
    private String remark;
    /** 是否置顶：0否 1是 */
    private Integer pinned; // 0/1
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 置顶时间 */
    private Date pinnedTime;
    /** 删除标志：0存在 2删除 */
    private String delFlag;

    // 查询辅助
    /** 门户列表：是否包含过期（查询用） */
    private Boolean includeExpired; // 门户列表：是否包含过期
    /** 仅过期（查询用） */
    private Boolean expiredOnly;    // 仅过期

    // 门户辅助：是否已提交（0/1）
    /** 门户辅助：当前用户是否已提交（0/1） */
    private Integer submitted;

    // 详情聚合
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 题目列表（详情返回） */
    private List<SurveyItem> items;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 可见范围（详情返回） */
    private List<SurveyScope> scopes;

    // 门户：我的作答（仅详情返回），key=itemId；value 为字符串或 Long[]
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 我的作答（详情返回），key=itemId；value 为字符串或 Long[] */
    private Map<Long, Object> myAnswers;
}
