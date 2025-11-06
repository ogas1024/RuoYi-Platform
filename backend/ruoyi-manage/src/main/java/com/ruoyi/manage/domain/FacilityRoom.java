package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilityRoom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long buildingId;
    private Integer floorNo;
    private String roomName;
    private Integer capacity;
    private String equipmentTags;
    private String status; // 0启用 1禁用
    private String remark;
    private String delFlag; // 0存在 2删除

    // 查询辅助字段
    private Long buildingIdEq; // 用于列表按楼栋过滤
    private Integer floorNoEq; // 用于列表按楼层过滤
}

