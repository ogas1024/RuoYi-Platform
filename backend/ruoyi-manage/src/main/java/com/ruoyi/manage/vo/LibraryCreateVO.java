package com.ruoyi.manage.vo;

import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import lombok.Data;

import java.util.List;

/**
 * 门户一次性创建图书并附带资产的请求体
 */
@Data
public class LibraryCreateVO extends Library {
    private static final long serialVersionUID = 1L;
    private List<LibraryAsset> assets;
}

