package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.LostItem;
import com.ruoyi.manage.domain.LostItemImage;

import java.util.List;

public interface ILostItemService {
    LostItem selectById(Long id);
    List<LostItem> selectList(LostItem query, List<Integer> statusList);
    List<LostItemImage> listImages(Long itemId);

    int insert(LostItem data);
    int update(LostItem data);
    int softDeleteById(Long id, String username, boolean admin);

    int approve(Long id, String auditBy);
    int reject(Long id, String auditBy, String reason);
    int offline(Long id, String auditBy, String reason);
    int restore(Long id, String auditBy);
    int solve(Long id, String username);

    int setSolvedAdmin(Long id, boolean solved, String auditBy);
}
