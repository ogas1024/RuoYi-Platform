package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class LibraryFavorite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private Long userId;
    private String delFlag;
}
