package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.LostItem;
import com.ruoyi.manage.domain.LostItemImage;
import com.ruoyi.manage.mapper.LostItemImageMapper;
import com.ruoyi.manage.mapper.LostItemMapper;
import com.ruoyi.manage.service.ILostItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class LostItemServiceImpl implements ILostItemService {

    @Resource
    private LostItemMapper mapper;
    @Resource
    private LostItemImageMapper imageMapper;

    @Override
    public LostItem selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public List<LostItem> selectList(LostItem query, List<Integer> statusList) {
        return mapper.selectList(query, statusList);
    }

    @Override
    public List<LostItemImage> listImages(Long itemId) {
        return imageMapper.listByItemId(itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(LostItem data) {
        if (data.getStatus() == null) data.setStatus(1); // 待审
        if (data.getSolvedFlag() == null) data.setSolvedFlag(0);
        if (data.getViews() == null) data.setViews(0);
        data.setCreateTime(new Date());
        int n = mapper.insert(data);
        if (n > 0 && data.getImages() != null && !data.getImages().isEmpty()) {
            for (int i = 0; i < data.getImages().size(); i++) {
                LostItemImage img = data.getImages().get(i);
                img.setItemId(data.getId());
                if (img.getSortNo() == null) img.setSortNo(i);
                img.setCreateBy(data.getCreateBy());
                img.setCreateTime(data.getCreateTime());
            }
            imageMapper.batchInsert(data.getImages());
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(LostItem data) {
        data.setUpdateTime(new Date());
        int n = mapper.update(data);
        if (n > 0 && data.getImages() != null) {
            imageMapper.deleteByItemId(data.getId());
            if (!data.getImages().isEmpty()) {
                for (int i = 0; i < data.getImages().size(); i++) {
                    LostItemImage img = data.getImages().get(i);
                    img.setItemId(data.getId());
                    if (img.getSortNo() == null) img.setSortNo(i);
                    img.setCreateBy(data.getUpdateBy());
                    img.setCreateTime(data.getUpdateTime());
                }
                imageMapper.batchInsert(data.getImages());
            }
        }
        return n;
    }

    @Override
    public int softDeleteById(Long id, String username, boolean admin) {
        return mapper.softDeleteById(id, username, admin);
    }

    @Override
    public int approve(Long id, String auditBy) {
        return mapper.approve(id, auditBy, new Date());
    }

    @Override
    public int reject(Long id, String auditBy, String reason) {
        return mapper.reject(id, auditBy, new Date(), reason);
    }

    @Override
    public int offline(Long id, String auditBy, String reason) {
        return mapper.offline(id, auditBy, new Date(), reason);
    }

    @Override
    public int restore(Long id, String auditBy) {
        return mapper.restore(id, auditBy, new Date());
    }

    @Override
    public int solve(Long id, String username) {
        return mapper.solve(id, username);
    }

    @Override
    public int setSolvedAdmin(Long id, boolean solved, String auditBy) {
        return mapper.setSolvedAdmin(id, solved ? 1 : 0, auditBy);
    }
}
