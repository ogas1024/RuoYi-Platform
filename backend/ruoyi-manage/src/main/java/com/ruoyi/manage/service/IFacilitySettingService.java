package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilitySetting;

/**
 * 功能房-设置 服务接口
 */
public interface IFacilitySettingService {
    /**
     * 获取设置（单例）
     * @return 设置实体，若不存在可返回默认值
     */
    FacilitySetting get();

    /**
     * 保存设置（存在则更新，不存在则插入）
     * @param data 设置实体
     * @return 影响行数
     */
    int save(FacilitySetting data);
}
