package com.ruoyi.manage.domain.vo;

import lombok.Data;

/**
 * 简单的按天计数返回对象
 */
@Data
public class DayCount {
    /** yyyy-MM-dd */
    private String day;
    /** 数量 */
    private Long count;
}

