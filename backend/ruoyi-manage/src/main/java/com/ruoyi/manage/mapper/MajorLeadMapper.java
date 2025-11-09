package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.domain.Major;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专业负责人映射 Mapper
 */
public interface MajorLeadMapper {
    MajorLead selectById(Long id);

    List<MajorLead> selectList(MajorLead query);

    int insert(MajorLead data);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);

    int deleteByMajorAndUser(@Param("majorId") Long majorId, @Param("userId") Long userId);

    Integer countByUserId(@Param("userId") Long userId);

    List<Long> selectUserIdsByIds(@Param("ids") Long[] ids);

    int deleteByUserId(@Param("userId") Long userId);

    /** 查询用户被分配的专业列表 */
    List<Major> selectMajorsByUserId(@Param("userId") Long userId);

    /** 是否存在某用户-专业的映射 */
    Integer existsUserMajor(@Param("userId") Long userId, @Param("majorId") Long majorId);
}
