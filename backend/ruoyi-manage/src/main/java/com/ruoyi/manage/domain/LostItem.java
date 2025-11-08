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

    private Long id;
    /**
     * lost/ found
     */
    private String type;
    private String title;
    private String content;
    /**
     * 联系信息（≤50字）
     */
    private String contactInfo;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lostTime;

    private Integer views;
    /**
     * 0草稿 1待审 2已发 3驳回 4下架
     */
    private Integer status;
    /**
     * 0/1
     */
    private Integer solvedFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    private String rejectReason;
    private String offlineReason;
    private String delFlag; // 0存在 2删除

    // 查询辅助字段（不入库）
    private String keyword; // 匹配：title/content/contactInfo
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime; // 发生时间-起
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;   // 发生时间-止

    // 详情聚合（仅详情/编辑接口返回）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<LostItemImage> images;
}
