package com.ruoyi.manage.domain.vo;

import lombok.Data;

/**
 * ECharts 饼图项：name + value（可选 id）
 */
@Data
public class PieItem {
    private Long id;
    private String name;
    private Long value;
}

