package com.ruoyi.manage.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class Category extends BaseEntity {
    private Long id;
    private String categoryName;
    private Integer bookCount;
}
