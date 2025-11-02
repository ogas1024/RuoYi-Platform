package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String contentHtml;
    private Integer type; // 1通知 2公告
    private Integer status; // 0草稿 1已发布 2撤回 3已过期（计算型）
    private Integer visibleAll; // 1全员 0自定义
    private Long publisherId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    private Integer pinned; // 0/1
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pinnedTime;
    private Integer editCount;
    private Integer readCount;
    private Integer attachmentCount;
    private String delFlag; // 0存在 2删除

    // 查询辅助
    private String keyword; // 按标题搜索
    private Boolean includeExpired; // 是否包含过期

    // 计算展示
    private Boolean read;     // 当前用户是否已读
    private Boolean expired;  // 是否已过期

    // 详情聚合（仅详情接口返回）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NoticeAttachment> attachments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NoticeScope> scopes;
}

