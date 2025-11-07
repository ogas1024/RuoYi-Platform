package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LostItemImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LostItemImageMapper {
    List<LostItemImage> listByItemId(@Param("itemId") Long itemId);
    int deleteByItemId(@Param("itemId") Long itemId);
    int batchInsert(@Param("list") List<LostItemImage> list);
}

