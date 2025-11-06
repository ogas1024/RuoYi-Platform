package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityBuilding;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FacilityBuildingMapper {
    FacilityBuilding selectById(Long id);
    List<FacilityBuilding> selectList(@Param("query") FacilityBuilding query);
    int insert(FacilityBuilding data);
    int update(FacilityBuilding data);
    int softDeleteByIds(@Param("ids") Long[] ids);
}

