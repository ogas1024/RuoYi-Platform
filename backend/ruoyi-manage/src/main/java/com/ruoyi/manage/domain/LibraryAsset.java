package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class LibraryAsset extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 图书ID（tb_library_book.id） */
    private Long bookId;
    /** 资产类型：'0' 文件, '1' 外链 */
    private String assetType; // '0' 文件, '1' 外链
    /** 文件格式：pdf/epub/mobi/zip */
    private String format;    // pdf/epub/mobi/zip
    /** 文件直链（类型为文件时） */
    private String fileUrl;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 文件哈希（sha256 等） */
    private String fileHash;
    /** OSS 对象键（可选，用于硬删） */
    private String ossObjectKey;
    /** 外链URL（类型为外链时） */
    private String linkUrl;
    /** 排序号（小到大） */
    private Integer sort;
    /** 删除标志：0/2 */
    private String delFlag;
}
