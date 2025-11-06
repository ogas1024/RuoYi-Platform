package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilitySetting;

public interface FacilitySettingMapper {
    FacilitySetting selectOne();
    int upsert(FacilitySetting data);
}

