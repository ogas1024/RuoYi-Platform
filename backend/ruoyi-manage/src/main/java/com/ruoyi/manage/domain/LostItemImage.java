package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LostItemImage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long itemId;
    private String url;
    private Integer sortNo;
    private String delFlag; // 0存在 2删除
}
