package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LostItemImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 失物招领-图片 Mapper
 */
public interface LostItemImageMapper {
    List<LostItemImage> listByItemId(@Param("itemId") Long itemId);

    int deleteByItemId(@Param("itemId") Long itemId);

    int batchInsert(@Param("list") List<LostItemImage> list);
}
