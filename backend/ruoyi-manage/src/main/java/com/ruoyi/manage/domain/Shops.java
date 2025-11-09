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
public class Shops extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 对应 sys_user.user_id */
    private Long id;
    /** 店铺名称 */
    private String shopName;
    /** 联系人 */
    private String contactPerson;
    /** 类别ID */
    private Long categoryId;
    /** 联系电话 */
    private String contactPhone;
    /** 类别名称（冗余） */
    private String categoryName;

    /** 绑定的登录账号（用于创建卖家用户） */
    private String userName;
    /** 初始密码（提交时传入，服务层会加密） */
    private String password;
}
