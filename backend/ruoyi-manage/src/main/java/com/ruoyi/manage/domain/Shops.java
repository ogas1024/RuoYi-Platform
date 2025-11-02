package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商铺信息对象 tb_shops
 * 
 * @author 曾辉
 * @date 2025-10-12
 */
@Data
public class Shops extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 对应Sys_User表的User_id */
    private Long id;
    private String shopName;
    private String contactPerson;
    private Long categoryId;
    private String contactPhone;
    private String categoryName;

    private String userName;
    private String password;
}
