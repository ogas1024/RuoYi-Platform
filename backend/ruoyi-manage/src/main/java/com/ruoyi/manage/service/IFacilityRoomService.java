package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityRoom;

import java.util.List;

public interface IFacilityRoomService {
    List<FacilityRoom> selectList(FacilityRoom query);

    FacilityRoom selectById(Long id);

    int insert(FacilityRoom data);

    int update(FacilityRoom data);

    int deleteByIds(Long[] ids);

    int enable(Long id, boolean enable);
}

