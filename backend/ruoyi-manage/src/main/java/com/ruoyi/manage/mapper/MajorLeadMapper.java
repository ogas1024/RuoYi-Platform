package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.MajorLead;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MajorLeadMapper {
    MajorLead selectById(Long id);
    List<MajorLead> selectList(MajorLead query);
    int insert(MajorLead data);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
    int deleteByMajorAndUser(@Param("majorId") Long majorId, @Param("userId") Long userId);
}

