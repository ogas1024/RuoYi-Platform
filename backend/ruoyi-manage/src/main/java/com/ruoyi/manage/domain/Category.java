package com.ruoyi.manage.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class Category extends BaseEntity {
    /** 主键ID */
    private Long id;
    /** 类别名称（唯一） */
    private String categoryName;
    /** 图书数量（统计冗余） */
    private Integer bookCount;
}
