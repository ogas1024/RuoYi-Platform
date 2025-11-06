package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBuilding;

import java.util.List;

public interface IFacilityBuildingService {
    List<FacilityBuilding> selectList(FacilityBuilding query);
    FacilityBuilding selectById(Long id);
    int insert(FacilityBuilding data);
    int update(FacilityBuilding data);
    int deleteByIds(Long[] ids);
}

