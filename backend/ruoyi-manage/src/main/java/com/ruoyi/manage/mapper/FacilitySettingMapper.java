package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilitySetting;

/**
 * 功能房-设置 Mapper
 */
public interface FacilitySettingMapper {
    FacilitySetting selectOne();

    int upsert(FacilitySetting data);
}
