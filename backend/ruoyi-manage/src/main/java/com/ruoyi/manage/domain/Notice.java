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

    /** 主键ID */
    private Long id;
    /** 标题 */
    private String title;
    /** 富文本内容（HTML） */
    private String contentHtml;
    /** 类型：1通知 2公告 */
    private Integer type; // 1通知 2公告
    /** 状态：0草稿 1已发布 2撤回 3已过期（计算型） */
    private Integer status; // 0草稿 1已发布 2撤回 3已过期（计算型）
    /** 是否全员可见：1是 0否（自定义范围） */
    private Integer visibleAll; // 1全员 0自定义
    /** 发布者用户ID */
    private Long publisherId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 发布时间 */
    private Date publishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 过期时间（可选） */
    private Date expireTime;
    /** 是否置顶：0否 1是 */
    private Integer pinned; // 0/1
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 置顶时间 */
    private Date pinnedTime;
    /** 编辑次数 */
    private Integer editCount;
    /** 阅读次数 */
    private Integer readCount;
    /** 附件数量 */
    private Integer attachmentCount;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 查询辅助
    /** 标题关键字（查询用） */
    private String keyword; // 按标题搜索
    /** 是否包含过期（查询用） */
    private Boolean includeExpired; // 是否包含过期
    /** 已读/未读筛选（查询用），结果也用该字段承载 */
    private Boolean read; // 查询：已读/未读（true/false）；结果也用该字段承载
    /** 排序字段：publishTime/updateTime/expireTime */
    private String orderBy; // 排序字段：publishTime/updateTime/expireTime
    /** 排序方向：asc/desc */
    private String orderDir; // 排序方向：asc/desc

    // 计算展示
    /** 是否已过期（计算字段） */
    private Boolean expired;  // 是否已过期

    // 详情聚合（仅详情接口返回）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 附件列表（详情返回） */
    private List<NoticeAttachment> attachments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 可见范围（详情返回） */
    private List<NoticeScope> scopes;
}
