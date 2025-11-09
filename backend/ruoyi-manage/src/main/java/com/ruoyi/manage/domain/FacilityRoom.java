package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class FacilityRoom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 楼房ID（tb_facility_building.id） */
    private Long buildingId;
    /** 楼层号（整数） */
    private Integer floorNo;
    /** 房间名称 */
    private String roomName;
    /** 容量（人数） */
    private Integer capacity;
    /** 设备标签（英文逗号分隔） */
    private String equipmentTags;
    /** 状态：0启用 1禁用 */
    private String status; // 0启用 1禁用
    /** 备注 */
    private String remark;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 查询辅助字段
    /** 查询：按楼栋过滤 */
    private Long buildingIdEq; // 用于列表按楼栋过滤
    /** 查询：按楼层过滤 */
    private Integer floorNoEq; // 用于列表按楼层过滤
}
