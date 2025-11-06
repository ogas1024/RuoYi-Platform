package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityBan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FacilityBanMapper {
    int insert(FacilityBan data);
    int deleteById(@Param("id") Long id);
    List<FacilityBan> selectList(@Param("status") String status, @Param("userId") Long userId);
    int countActive(@Param("userId") Long userId);
}

