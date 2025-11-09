package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Major;

import java.util.List;

/**
 * 专业服务接口
 * 说明：管理 tb_major 的基础增删改查。
 */
public interface IMajorService {
    /**
     * 按主键查询专业
     * @param id 专业ID
     * @return 专业实体
     */
    Major selectMajorById(Long id);

    /**
     * 查询专业列表
     * @param major 查询条件（名称、状态等）
     * @return 专业集合
     */
    List<Major> selectMajorList(Major major);

    /**
     * 新增专业
     * @param major 实体
     * @return 影响行数
     */
    int insertMajor(Major major);

    /**
     * 修改专业
     * @param major 实体
     * @return 影响行数
     */
    int updateMajor(Major major);

    /**
     * 批量删除专业
     * @param ids ID数组
     * @return 影响行数
     */
    int deleteMajorByIds(Long[] ids);
}
