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

    private Long id;
    private String title;
    private Integer status; // 0草稿 1发布 2归档
    private Integer visibleAll; // 1全员 0自定义
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline; // 截止时间
    private String remark;
    private Integer pinned; // 0/1
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pinnedTime;
    private String delFlag;

    // 查询辅助
    private Boolean includeExpired; // 门户列表：是否包含过期
    private Boolean expiredOnly;    // 仅过期

    // 门户辅助：是否已提交（0/1）
    private Integer submitted;

    // 详情聚合
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SurveyItem> items;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SurveyScope> scopes;

    // 门户：我的作答（仅详情返回），key=itemId；value 为字符串或 Long[]
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Long, Object> myAnswers;
}
