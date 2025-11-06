package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LibraryAsset;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LibraryAssetMapper {
    List<LibraryAsset> selectByBookId(@Param("bookId") Long bookId);
    int insert(LibraryAsset asset);
    int deleteById(@Param("id") Long id);
}
