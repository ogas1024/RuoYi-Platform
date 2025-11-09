package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LostItemImage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 失物条目ID */
    private Long itemId;
    /** 图片URL */
    private String url;
    /** 排序号（升序） */
    private Integer sortNo;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除
}
