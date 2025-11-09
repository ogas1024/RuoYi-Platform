package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.mapper.MajorMapper;
import com.ruoyi.manage.mapper.CourseMapper;
import com.ruoyi.manage.service.IMajorService;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 专业 服务实现
 */
@Service
public class MajorServiceImpl implements IMajorService {

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Major selectMajorById(Long id) {
        return majorMapper.selectMajorById(id);
    }

    @Override
    public List<Major> selectMajorList(Major major) {
        return majorMapper.selectMajorList(major);
    }

    @Override
    public int insertMajor(Major major) {
        return majorMapper.insertMajor(major);
    }

    @Override
    public int updateMajor(Major major) {
        return majorMapper.updateMajor(major);
    }

    @Override
    public int deleteMajorByIds(Long[] ids) {
        // 校验：若专业下存在课程，禁止删除
        if (ids != null && ids.length > 0) {
            int courseCount = courseMapper.countByMajorIds(ids);
            if (courseCount > 0) {
                throw new ServiceException("所选专业下存在课程，无法删除");
            }
        }
        return majorMapper.deleteMajorByIds(ids);
    }
}
