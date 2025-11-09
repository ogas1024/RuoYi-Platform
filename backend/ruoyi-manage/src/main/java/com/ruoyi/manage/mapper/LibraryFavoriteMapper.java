package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LibraryFavorite;
import org.apache.ibatis.annotations.Param;

/**
 * 图书收藏 Mapper
 */
public interface LibraryFavoriteMapper {
    Integer exists(@Param("bookId") Long bookId, @Param("userId") Long userId);

    int insert(LibraryFavorite fav);

    int delete(@Param("bookId") Long bookId, @Param("userId") Long userId);
}
