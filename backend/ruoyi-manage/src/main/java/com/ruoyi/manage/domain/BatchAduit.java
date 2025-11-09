package com.ruoyi.manage.domain;

import lombok.Data;

import java.util.List;

@Data
public class BatchAduit {
    /** 待处理的主键集合 */
    private Long[] ids;
    /** 目标状态（业务自定义） */
    private Integer status;
}
