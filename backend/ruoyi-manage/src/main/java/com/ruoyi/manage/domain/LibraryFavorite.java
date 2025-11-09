package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class LibraryFavorite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 图书ID */
    private Long bookId;
    /** 用户ID */
    private Long userId;
    /** 删除标志 */
    private String delFlag;
}
