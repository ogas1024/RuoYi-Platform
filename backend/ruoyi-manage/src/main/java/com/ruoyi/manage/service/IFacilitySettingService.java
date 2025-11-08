package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilitySetting;

public interface IFacilitySettingService {
    FacilitySetting get();

    int save(FacilitySetting data);
}

