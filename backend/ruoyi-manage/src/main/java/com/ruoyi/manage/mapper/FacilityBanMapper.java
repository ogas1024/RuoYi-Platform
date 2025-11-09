package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityBan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能房-封禁 Mapper
 */
public interface FacilityBanMapper {
    int insert(FacilityBan data);

    int deleteById(@Param("id") Long id);

    List<FacilityBan> selectList(@Param("status") String status, @Param("userId") Long userId, @Param("username") String username);

    int countActive(@Param("userId") Long userId);
}
