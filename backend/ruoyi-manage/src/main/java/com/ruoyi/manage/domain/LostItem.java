package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class LostItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 类型：lost-招领（丢失）、found-拾获 */
    private String type;
    /** 标题 */
    private String title;
    /** 正文内容（描述物品/拾取/丢失信息） */
    private String content;
    /** 联系方式（≤50 字，手机号/微信等） */
    private String contactInfo;
    /** 地点描述（可选） */
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 发生时间（可选） */
    private Date lostTime;

    /** 浏览次数 */
    private Integer views;
    /** 状态：0草稿 1待审 2已发布 3驳回 4已下架 */
    private Integer status;
    /** 是否已解决：0-否 1-是 */
    private Integer solvedFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 首次发布上线时间 */
    private Date publishTime;
    /** 驳回原因 */
    private String rejectReason;
    /** 下架原因 */
    private String offlineReason;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 查询辅助字段（不入库）
    /** 关键字搜索：匹配 title/content/contactInfo */
    private String keyword; // 匹配：title/content/contactInfo
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 发生时间-起（查询专用） */
    private Date beginTime; // 发生时间-起
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 发生时间-止（查询专用） */
    private Date endTime;   // 发生时间-止

    // 详情聚合（仅详情/编辑接口返回）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** 图片列表（详情/编辑页返回） */
    private List<LostItemImage> images;
}
