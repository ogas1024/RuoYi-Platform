package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class LibraryAsset extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private String assetType; // '0' 文件, '1' 外链
    private String format;    // pdf/epub/mobi/zip
    private String fileUrl;
    private Long fileSize;
    private String fileHash;
    private String ossObjectKey;
    private String linkUrl;
    private Integer sort;
    private String delFlag;
}
